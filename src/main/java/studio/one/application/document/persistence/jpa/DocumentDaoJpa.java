package studio.one.application.document.persistence.jpa;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import studio.one.application.document.persistence.DocumentDao;
import studio.one.application.document.command.CreateDocumentCommand;
import studio.one.application.document.command.CreateVersionCommand;
import studio.one.application.document.command.CreateBlockCommand;
import studio.one.application.document.command.DeleteBlockCommand;
import studio.one.application.document.command.DeleteDocumentCommand;
import studio.one.application.document.command.MoveBlockCommand;
import studio.one.application.document.command.UpdateBlockCommand;
import studio.one.application.document.command.UpdateDocumentMetaCommand;
import studio.one.application.document.persistence.jpa.entity.BodyVersionId;
import studio.one.application.document.persistence.jpa.entity.DocumentBodyEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentBodyVersionEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentBlockEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentBlockVersionEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentBlockVersionId;
import studio.one.application.document.persistence.jpa.entity.DocumentEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentPropertyEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentVersionEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentVersionId;
import studio.one.application.document.persistence.jpa.entity.PropertyId;
import studio.one.application.document.persistence.jpa.repo.DocumentBodyRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBodyVersionRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBlockRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBlockVersionRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentPropertyRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentSummaryProjection;
import studio.one.application.document.persistence.jpa.repo.DocumentVersionRepository;
import studio.one.application.document.domain.model.DocumentBlock;
import studio.one.application.document.domain.exception.BlockConflictException;
import studio.one.application.document.domain.exception.DocumentConflictException;
import studio.one.application.document.domain.model.Document;
import studio.one.application.document.domain.model.DocumentSummary;
import studio.one.application.document.domain.model.DocumentVersion;
import studio.one.application.document.domain.model.DocumentVersionBundle;
import studio.one.application.document.domain.exception.DocumentNotFoundException;

public class DocumentDaoJpa implements DocumentDao {

    private final DocumentRepository docRepo;
    private final DocumentVersionRepository verRepo;
    private final DocumentBodyRepository bodyRepo;
    private final DocumentBodyVersionRepository bodyVerRepo;
    private final DocumentPropertyRepository propRepo;
    private final DocumentBlockRepository blockRepo;
    private final DocumentBlockVersionRepository blockVersionRepo;
    private final EntityManager em;

    public DocumentDaoJpa(DocumentRepository docRepo,
                          DocumentVersionRepository verRepo,
                          DocumentBodyRepository bodyRepo,
                          DocumentBodyVersionRepository bodyVerRepo,
                          DocumentPropertyRepository propRepo,
                          DocumentBlockRepository blockRepo,
                          DocumentBlockVersionRepository blockVersionRepo,
                          EntityManager em) {
        this.docRepo = docRepo;
        this.verRepo = verRepo;
        this.bodyRepo = bodyRepo;
        this.bodyVerRepo = bodyVerRepo;
        this.propRepo = propRepo;
        this.blockRepo = blockRepo;
        this.blockVersionRepo = blockVersionRepo;
        this.em = em;
    }

    @Override
    @Transactional
    public long createDocument(CreateDocumentCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();

        DocumentEntity d = new DocumentEntity();
        d.setObjectType(cmd.getObjectType());
        d.setObjectId(cmd.getObjectId());
        d.setParentDocumentId(cmd.getParentDocumentId());
        d.setSortOrder(cmd.getSortOrder() == null ? 0 : cmd.getSortOrder());
        d.setName(cmd.getName());
        d.setCurrentVersionId(1);
        d.setReadCount(0L);
        d.setCreatedBy(cmd.getActorUserId());
        d.setCreatedAt(now);
        d = docRepo.save(d);

        DocumentVersionEntity v = new DocumentVersionEntity();
        v.setId(new DocumentVersionId(d.getDocumentId(), 1));
        v.setTitle(cmd.getTitle());
        v.setSecured(true);
        v.setCreatedBy(cmd.getActorUserId());
        v.setCreatedAt(now);
        verRepo.save(v);

        DocumentBodyEntity body = new DocumentBodyEntity();
        body.setDocumentId(d.getDocumentId());
        body.setBodyType(cmd.getBodyType());
        body.setBodyText(cmd.getBodyText());
        body.setCreatedBy(cmd.getActorUserId());
        body.setCreatedAt(now);
        body = bodyRepo.save(body);

        DocumentBodyVersionEntity bv = new DocumentBodyVersionEntity();
        bv.setId(new BodyVersionId(body.getBodyId(), d.getDocumentId(), 1));
        bv.setCreatedBy(cmd.getActorUserId());
        bv.setCreatedAt(now);
        bodyVerRepo.save(bv);

        Map<String, String> props = cmd.getProperties();
        if (props != null && !props.isEmpty()) {
            for (var e : props.entrySet()) {
                DocumentPropertyEntity p = new DocumentPropertyEntity();
                p.setId(new PropertyId(d.getDocumentId(), 1, e.getKey()));
                p.setValue(e.getValue());
                p.setCreatedBy(cmd.getActorUserId());
                p.setCreatedAt(now);
                propRepo.save(p);
            }
        }

        return d.getDocumentId();
    }

    @Override
    @Transactional
    public int createNewVersion(long documentId, CreateVersionCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();

        DocumentEntity doc = docRepo.findByIdForUpdate(documentId)
            .orElseThrow(() -> DocumentNotFoundException.byId(documentId));

        int currentVersionId = doc.getCurrentVersionId();
        int newVersion = doc.getCurrentVersionId() + 1;

        DocumentVersionEntity v = new DocumentVersionEntity();
        v.setId(new DocumentVersionId(documentId, newVersion));
        v.setTitle(cmd.getTitle());
        v.setSecured(true);
        v.setCreatedBy(cmd.getActorUserId());
        v.setCreatedAt(now);
        verRepo.save(v);

        DocumentBodyEntity body = new DocumentBodyEntity();
        body.setDocumentId(documentId);
        body.setBodyType(cmd.getBodyType());
        body.setBodyText(cmd.getBodyText());
        body.setCreatedBy(cmd.getActorUserId());
        body.setCreatedAt(now);
        body = bodyRepo.save(body);

        DocumentBodyVersionEntity bv = new DocumentBodyVersionEntity();
        bv.setId(new BodyVersionId(body.getBodyId(), documentId, newVersion));
        bv.setCreatedBy(cmd.getActorUserId());
        bv.setCreatedAt(now);
        bodyVerRepo.save(bv);

        Map<String, String> props = cmd.getProperties();
        if (props != null && !props.isEmpty()) {
            for (var e : props.entrySet()) {
                DocumentPropertyEntity p = new DocumentPropertyEntity();
                p.setId(new PropertyId(documentId, newVersion, e.getKey()));
                p.setValue(e.getValue());
                p.setCreatedBy(cmd.getActorUserId());
                p.setCreatedAt(now);
                propRepo.save(p);
            }
        }

        doc.setCurrentVersionId(newVersion);
        doc.setUpdatedBy(cmd.getActorUserId());
        doc.setUpdatedAt(now);
        docRepo.save(doc);

        copyBlockVersions(documentId, currentVersionId, newVersion, cmd.getActorUserId(), now);

        return newVersion;
    }

    @Override
    public Optional<Document> findDocument(long documentId) {
        return docRepo.findById(documentId).map(this::toDomain);
    }

    @Override
    public Optional<DocumentVersion> findVersion(long documentId, int versionId) {
        return verRepo.findById(new DocumentVersionId(documentId, versionId)).map(this::toDomain);
    }

    @Override
    public Optional<DocumentVersionBundle> findVersionBundle(long documentId, int versionId) {
        Optional<Document> doc = findDocument(documentId);
        Optional<DocumentVersion> ver = findVersion(documentId, versionId);
        if (doc.isEmpty() || ver.isEmpty()) return Optional.empty();

        Query q = em.createNativeQuery(
            "SELECT b.\"BODY_TYPE\", b.\"BODY_TEXT\" " +
            "FROM \"TB_APPLICATION_DOCUMENT_BODY_VERSION\" bv " +
            "JOIN \"TB_APPLICATION_DOCUMENT_BODY\" b ON b.\"BODY_ID\" = bv.\"BODY_ID\" " +
            "WHERE bv.\"DOCUMENT_ID\" = :documentId AND bv.\"VERSION_ID\" = :versionId " +
            "ORDER BY bv.\"CREATED_AT\" DESC LIMIT 1"
        );
        q.setParameter("documentId", documentId);
        q.setParameter("versionId", versionId);
        List<?> rows = q.getResultList();

        Integer bodyType = null;
        String bodyText = null;
        if (!rows.isEmpty()) {
            Object[] r = (Object[]) rows.get(0);
            bodyType = r[0] == null ? null : ((Number) r[0]).intValue();
            bodyText = r[1] == null ? null : r[1].toString();
        }

        Map<String, String> props = new LinkedHashMap<>();
        Query pq = em.createNativeQuery(
            "SELECT \"PROPERTY_NAME\", \"PROPERTY_VALUE\" FROM \"TB_APPLICATION_DOCUMENT_PROPERTY\" " +
            "WHERE \"DOCUMENT_ID\" = :documentId AND \"VERSION_ID\" = :versionId ORDER BY \"PROPERTY_NAME\""
        );
        pq.setParameter("documentId", documentId);
        pq.setParameter("versionId", versionId);
        for (Object row : pq.getResultList()) {
            Object[] r = (Object[]) row;
            props.put(r[0].toString(), r[1] == null ? null : r[1].toString());
        }

        return Optional.of(new DocumentVersionBundle(doc.get(), ver.get(), bodyType, bodyText, props));
    }

    @Override
    public Optional<DocumentVersionBundle> findLatestBundle(long documentId) {
        Optional<Document> doc = findDocument(documentId);
        if (doc.isEmpty()) return Optional.empty();
        return findVersionBundle(documentId, doc.get().getCurrentVersionId());
    }

    @Override
    public List<DocumentVersion> listVersions(long documentId) {
        List<DocumentVersionEntity> entities = verRepo.findByIdDocumentIdOrderByIdVersionIdDesc(documentId);
        List<DocumentVersion> out = new ArrayList<>();
        for (var e : entities) out.add(toDomain(e));
        return out;
    }

    @Override
    public Page<Document> findAll(Pageable pageable) {
        return docRepo.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Page<Document> findByNameOrBody(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(pageable);
        }
        String like = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
        return docRepo.findByNameOrBody(like, pageable).map(this::toDomain);
    }

    @Override
    public Page<Document> findByObjectTypeAndObjectId(int objectType, long objectId, Pageable pageable) {
        return docRepo.findByObjectTypeAndObjectId(objectType, objectId, pageable).map(this::toDomain);
    }

    @Override
    public Page<Document> findByParentDocumentId(Long parentDocumentId, Pageable pageable) {
        if (parentDocumentId == null) {
            return docRepo.findByParentDocumentIdIsNull(pageable).map(this::toDomain);
        }
        return docRepo.findByParentDocumentId(parentDocumentId, pageable).map(this::toDomain);
    }

    @Override
    public Page<DocumentSummary> findSummaryAll(Pageable pageable) {
        return docRepo.findSummaryAll(pageable).map(this::toSummary);
    }

    @Override
    public Page<DocumentSummary> findSummaryByNameOrBody(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findSummaryAll(pageable);
        }
        String like = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
        return docRepo.findSummaryByNameOrBody(like, pageable).map(this::toSummary);
    }

    @Override
    public Page<DocumentSummary> findSummaryByObjectTypeAndObjectId(int objectType, long objectId, Pageable pageable) {
        return docRepo.findSummaryByObjectTypeAndObjectId(objectType, objectId, pageable).map(this::toSummary);
    }

    @Override
    public Page<DocumentSummary> findSummaryByParentDocumentId(Long parentDocumentId, Pageable pageable) {
        if (parentDocumentId == null) {
            return docRepo.findSummaryByParentDocumentIdIsNull(pageable).map(this::toSummary);
        }
        return docRepo.findSummaryByParentDocumentId(parentDocumentId, pageable).map(this::toSummary);
    }

    @Override
    @Transactional
    public void updateDocumentMeta(UpdateDocumentMetaCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        DocumentEntity doc = docRepo.findByIdForUpdate(cmd.getDocumentId())
            .orElseThrow(() -> DocumentNotFoundException.byId(cmd.getDocumentId()));
        assertNotStaleDocument(cmd.getExpectedUpdatedAt(), doc.getUpdatedAt(), cmd.getDocumentId());
        doc.setName(cmd.getName());
        doc.setPattern(cmd.getPattern());
        doc.setUpdatedBy(cmd.getActorUserId());
        doc.setUpdatedAt(now);
        docRepo.save(doc);
    }

    @Override
    @Transactional
    public void deleteDocument(DeleteDocumentCommand cmd) {
        DocumentEntity doc = docRepo.findByIdForUpdate(cmd.getDocumentId())
            .orElseThrow(() -> DocumentNotFoundException.byId(cmd.getDocumentId()));
        assertNotStaleDocument(cmd.getExpectedUpdatedAt(), doc.getUpdatedAt(), cmd.getDocumentId());
        Long documentId = cmd.getDocumentId();
        blockVersionRepo.deleteByIdDocumentId(documentId);
        blockRepo.deleteByDocumentId(documentId);
        bodyVerRepo.deleteByIdDocumentId(documentId);
        bodyRepo.deleteByDocumentId(documentId);
        propRepo.deleteByIdDocumentId(documentId);
        verRepo.deleteByIdDocumentId(documentId);
        docRepo.deleteById(documentId);
    }

    @Override
    @Transactional
    public long createBlock(CreateBlockCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        DocumentEntity doc = docRepo.findByIdForUpdate(cmd.getDocumentId())
            .orElseThrow(() -> DocumentNotFoundException.byId(cmd.getDocumentId()));
        assertParentBlock(cmd.getDocumentId(), cmd.getParentBlockId(), null);
        int sortOrder = resolveBlockSortOrder(cmd.getDocumentId(), doc.getCurrentVersionId(), cmd.getSortOrder());
        shiftBlockSortOrder(cmd.getDocumentId(), sortOrder, null);
        shiftBlockVersionSortOrder(cmd.getDocumentId(), doc.getCurrentVersionId(), sortOrder, null);
        DocumentBlockEntity block = new DocumentBlockEntity();
        block.setDocumentId(cmd.getDocumentId());
        block.setParentBlockId(cmd.getParentBlockId());
        block.setBlockType(cmd.getBlockType());
        block.setBlockData(cmd.getBlockData());
        block.setSortOrder(sortOrder);
        block.setIsDeleted(false);
        block.setCreatedBy(cmd.getActorUserId());
        block.setCreatedAt(now);
        block = blockRepo.save(block);
        DocumentBlockVersionEntity version = new DocumentBlockVersionEntity();
        version.setId(new DocumentBlockVersionId(block.getBlockId(), cmd.getDocumentId(), doc.getCurrentVersionId()));
        version.setParentBlockId(cmd.getParentBlockId());
        version.setBlockType(cmd.getBlockType());
        version.setBlockData(cmd.getBlockData());
        version.setSortOrder(sortOrder);
        version.setIsDeleted(false);
        version.setCreatedBy(cmd.getActorUserId());
        version.setCreatedAt(now);
        blockVersionRepo.save(version);
        return block.getBlockId();
    }

    @Override
    @Transactional
    public void updateBlock(UpdateBlockCommand cmd) {
        DocumentBlockEntity block = blockRepo.findById(cmd.getBlockId())
            .orElseThrow(() -> new NoSuchElementException("block not found: " + cmd.getBlockId()));
        assertNotStale(cmd.getExpectedUpdatedAt(), block.getUpdatedAt(), cmd.getBlockId());
        DocumentEntity doc = docRepo.findByIdForUpdate(block.getDocumentId())
            .orElseThrow(() -> DocumentNotFoundException.byId(block.getDocumentId()));
        assertParentBlock(block.getDocumentId(), cmd.getParentBlockId(), block.getBlockId());
        int nextSortOrder = cmd.getSortOrder() == null ? block.getSortOrder()
                : resolveBlockSortOrder(block.getDocumentId(), doc.getCurrentVersionId(), cmd.getSortOrder());
        if (Objects.equals(cmd.getParentBlockId(), block.getParentBlockId())
                && Objects.equals(nextSortOrder, block.getSortOrder())
                && Objects.equals(cmd.getBlockType(), block.getBlockType())
                && Objects.equals(cmd.getBlockData(), block.getBlockData())) {
            return;
        }
        if (nextSortOrder != block.getSortOrder()) {
            shiftBlockSortOrder(block.getDocumentId(), nextSortOrder, block.getBlockId());
            shiftBlockVersionSortOrder(block.getDocumentId(), doc.getCurrentVersionId(), nextSortOrder, block.getBlockId());
        }
        block.setParentBlockId(cmd.getParentBlockId());
        block.setBlockType(cmd.getBlockType());
        block.setBlockData(cmd.getBlockData());
        block.setSortOrder(nextSortOrder);
        block.setUpdatedBy(cmd.getActorUserId());
        block.setUpdatedAt(OffsetDateTime.now());
        blockRepo.save(block);
        DocumentBlockVersionEntity version = new DocumentBlockVersionEntity();
        version.setId(new DocumentBlockVersionId(block.getBlockId(), block.getDocumentId(), doc.getCurrentVersionId()));
        version.setParentBlockId(cmd.getParentBlockId());
        version.setBlockType(cmd.getBlockType());
        version.setBlockData(cmd.getBlockData());
        version.setSortOrder(nextSortOrder);
        version.setIsDeleted(false);
        version.setCreatedBy(cmd.getActorUserId());
        version.setCreatedAt(OffsetDateTime.now());
        DocumentBlockVersionEntity existing = blockVersionRepo.findById(version.getId()).orElse(null);
        if (existing != null && isSameBlockVersion(existing, version)) {
            return;
        }
        blockVersionRepo.save(version);
    }

    @Override
    @Transactional
    public void moveBlock(MoveBlockCommand cmd) {
        DocumentBlockEntity block = blockRepo.findById(cmd.getBlockId())
            .orElseThrow(() -> new NoSuchElementException("block not found: " + cmd.getBlockId()));
        assertNotStale(cmd.getExpectedUpdatedAt(), block.getUpdatedAt(), cmd.getBlockId());
        DocumentEntity doc = docRepo.findByIdForUpdate(block.getDocumentId())
            .orElseThrow(() -> DocumentNotFoundException.byId(block.getDocumentId()));
        assertParentBlock(block.getDocumentId(), cmd.getParentBlockId(), block.getBlockId());
        int nextSortOrder = cmd.getSortOrder() == null ? block.getSortOrder()
                : resolveBlockSortOrder(block.getDocumentId(), doc.getCurrentVersionId(), cmd.getSortOrder());
        if (Objects.equals(cmd.getParentBlockId(), block.getParentBlockId())
                && Objects.equals(nextSortOrder, block.getSortOrder())) {
            return;
        }
        if (nextSortOrder != block.getSortOrder()) {
            shiftBlockSortOrder(block.getDocumentId(), nextSortOrder, block.getBlockId());
            shiftBlockVersionSortOrder(block.getDocumentId(), doc.getCurrentVersionId(), nextSortOrder, block.getBlockId());
        }
        block.setParentBlockId(cmd.getParentBlockId());
        block.setSortOrder(nextSortOrder);
        block.setUpdatedBy(cmd.getActorUserId());
        block.setUpdatedAt(OffsetDateTime.now());
        blockRepo.save(block);
        DocumentBlockVersionEntity version = new DocumentBlockVersionEntity();
        version.setId(new DocumentBlockVersionId(block.getBlockId(), block.getDocumentId(), doc.getCurrentVersionId()));
        version.setParentBlockId(cmd.getParentBlockId());
        version.setBlockType(block.getBlockType());
        version.setBlockData(block.getBlockData());
        version.setSortOrder(nextSortOrder);
        version.setIsDeleted(false);
        version.setCreatedBy(cmd.getActorUserId());
        version.setCreatedAt(OffsetDateTime.now());
        DocumentBlockVersionEntity existing = blockVersionRepo.findById(version.getId()).orElse(null);
        if (existing != null && isSameBlockVersion(existing, version)) {
            return;
        }
        blockVersionRepo.save(version);
    }

    @Override
    @Transactional
    public void deleteBlock(DeleteBlockCommand cmd) {
        long blockId = cmd.getBlockId();
        DocumentBlockEntity block = blockRepo.findById(blockId)
            .orElseThrow(() -> new NoSuchElementException("block not found: " + blockId));
        assertNotStale(cmd.getExpectedUpdatedAt(), block.getUpdatedAt(), blockId);
        DocumentEntity doc = docRepo.findByIdForUpdate(block.getDocumentId())
            .orElseThrow(() -> DocumentNotFoundException.byId(block.getDocumentId()));
        block.setIsDeleted(true);
        block.setUpdatedBy(cmd.getActorUserId());
        block.setUpdatedAt(OffsetDateTime.now());
        blockRepo.save(block);
        DocumentBlockVersionEntity version = new DocumentBlockVersionEntity();
        version.setId(new DocumentBlockVersionId(blockId, block.getDocumentId(), doc.getCurrentVersionId()));
        version.setParentBlockId(block.getParentBlockId());
        version.setBlockType(block.getBlockType());
        version.setBlockData(block.getBlockData());
        version.setSortOrder(block.getSortOrder());
        version.setIsDeleted(true);
        version.setCreatedBy(cmd.getActorUserId());
        version.setCreatedAt(OffsetDateTime.now());
        blockVersionRepo.save(version);
        if (block.getSortOrder() != null) {
            shiftBlockSortOrderDown(block.getDocumentId(), block.getSortOrder());
            shiftBlockVersionSortOrderDown(block.getDocumentId(), doc.getCurrentVersionId(), block.getSortOrder());
        }
    }

    @Override
    public List<DocumentBlock> listBlocks(long documentId) {
        DocumentEntity doc = docRepo.findById(documentId)
            .orElseThrow(() -> DocumentNotFoundException.byId(documentId));
        return listBlocks(documentId, doc.getCurrentVersionId());
    }

    @Override
    public List<DocumentBlock> listBlocks(long documentId, int versionId) {
        List<DocumentBlockVersionEntity> entities =
            blockVersionRepo.findByIdDocumentIdAndIdVersionIdAndIsDeletedFalseOrderBySortOrderAscIdBlockIdAsc(documentId, versionId);
        List<DocumentBlock> out = new ArrayList<>();
        for (var e : entities) out.add(toDomain(e));
        return out;
    }

    @Override
    public List<DocumentBlock> listBlocksIncludingDeleted(long documentId, int versionId) {
        List<DocumentBlockVersionEntity> entities =
            blockVersionRepo.findByIdDocumentIdAndIdVersionIdOrderBySortOrderAscIdBlockIdAsc(documentId, versionId);
        List<DocumentBlock> out = new ArrayList<>();
        for (var e : entities) out.add(toDomain(e));
        return out;
    }

    private Document toDomain(DocumentEntity e) {
        return new Document(
            e.getDocumentId(),
            e.getObjectType(),
            e.getObjectId(),
            e.getParentDocumentId(),
            e.getSortOrder(),
            e.getName(),
            e.getCurrentVersionId(),
            e.getReadCount(),
            e.getPattern(),
            e.getCreatedBy(),
            e.getCreatedAt(),
            e.getUpdatedBy(),
            e.getUpdatedAt()
        );
    }

    private DocumentSummary toSummary(DocumentSummaryProjection p) {
        return new DocumentSummary(
            p.getDocumentId(),
            p.getObjectType(),
            p.getObjectId(),
            p.getParentDocumentId(),
            p.getSortOrder(),
            p.getName(),
            p.getTitle(),
            p.getLatestVersionId(),
            p.getCreatedBy() == null ? 0L : p.getCreatedBy(),
            p.getUpdatedBy(),
            p.getCreatedAt(),
            p.getUpdatedAt()
        );
    }

    private DocumentBlock toDomain(DocumentBlockEntity e) {
        return new DocumentBlock(
            e.getBlockId(),
            e.getDocumentId(),
            e.getParentBlockId(),
            e.getBlockType(),
            e.getBlockData(),
            e.getSortOrder(),
            e.getIsDeleted() != null && e.getIsDeleted(),
            e.getCreatedBy(),
            e.getCreatedAt(),
            e.getUpdatedBy(),
            e.getUpdatedAt()
        );
    }

    private DocumentBlock toDomain(DocumentBlockVersionEntity e) {
        return new DocumentBlock(
            e.getId().getBlockId(),
            e.getId().getDocumentId(),
            e.getParentBlockId(),
            e.getBlockType(),
            e.getBlockData(),
            e.getSortOrder(),
            e.getIsDeleted() != null && e.getIsDeleted(),
            e.getCreatedBy(),
            e.getCreatedAt(),
            null,
            null
        );
    }

    private boolean isSameBlockVersion(DocumentBlockVersionEntity current, DocumentBlockVersionEntity next) {
        if (current == null || next == null) {
            return false;
        }
        return Objects.equals(current.getParentBlockId(), next.getParentBlockId())
            && Objects.equals(current.getBlockType(), next.getBlockType())
            && Objects.equals(current.getBlockData(), next.getBlockData())
            && Objects.equals(current.getSortOrder(), next.getSortOrder())
            && Objects.equals(current.getIsDeleted(), next.getIsDeleted());
    }

    private int resolveBlockSortOrder(long documentId, int versionId, Integer desiredSortOrder) {
        if (desiredSortOrder != null) {
            return desiredSortOrder;
        }
        Integer max = (Integer) em.createNativeQuery(
            "SELECT COALESCE(MAX(\"SORT_ORDER\"), -1) " +
            "FROM \"TB_APPLICATION_DOCUMENT_BLOCK_VERSION\" " +
            "WHERE \"DOCUMENT_ID\" = :documentId AND \"VERSION_ID\" = :versionId AND \"IS_DELETED\" = FALSE")
            .setParameter("documentId", documentId)
            .setParameter("versionId", versionId)
            .getSingleResult();
        return (max == null ? -1 : max) + 1;
    }

    private void shiftBlockSortOrder(long documentId, int sortOrder, Long excludeBlockId) {
        String sql = "UPDATE \"TB_APPLICATION_DOCUMENT_BLOCK\" " +
            "SET \"SORT_ORDER\" = \"SORT_ORDER\" + 1 " +
            "WHERE \"DOCUMENT_ID\" = :documentId AND \"IS_DELETED\" = FALSE AND \"SORT_ORDER\" >= :sortOrder";
        if (excludeBlockId != null) {
            sql += " AND \"BLOCK_ID\" <> :blockId";
        }
        var q = em.createNativeQuery(sql)
            .setParameter("documentId", documentId)
            .setParameter("sortOrder", sortOrder);
        if (excludeBlockId != null) {
            q.setParameter("blockId", excludeBlockId);
        }
        q.executeUpdate();
    }

    private void assertNotStale(OffsetDateTime expected, OffsetDateTime actual, long blockId) {
        if (expected == null) {
            return;
        }
        if (actual == null || !expected.toInstant().equals(actual.toInstant())) {
            throw new BlockConflictException(blockId);
        }
    }

    private void assertNotStaleDocument(OffsetDateTime expected, OffsetDateTime actual, long documentId) {
        if (expected == null) {
            return;
        }
        if (actual == null || !expected.toInstant().equals(actual.toInstant())) {
            throw new DocumentConflictException(documentId);
        }
    }

    private void assertParentBlock(Long documentId, Long parentBlockId, Long blockId) {
        if (parentBlockId == null) {
            return;
        }
        if (blockId != null && Objects.equals(parentBlockId, blockId)) {
            throw new IllegalArgumentException("parent block cannot be self: " + blockId);
        }
        DocumentBlockEntity parent = blockRepo.findById(parentBlockId)
            .orElseThrow(() -> new IllegalArgumentException("parent block not found: " + parentBlockId));
        if (documentId != null && !Objects.equals(documentId, parent.getDocumentId())) {
            throw new IllegalArgumentException("parent block belongs to another document: " + parentBlockId);
        }
    }

    private void shiftBlockVersionSortOrder(long documentId, int versionId, int sortOrder, Long excludeBlockId) {
        String sql = "UPDATE \"TB_APPLICATION_DOCUMENT_BLOCK_VERSION\" " +
            "SET \"SORT_ORDER\" = \"SORT_ORDER\" + 1 " +
            "WHERE \"DOCUMENT_ID\" = :documentId AND \"VERSION_ID\" = :versionId " +
            "AND \"IS_DELETED\" = FALSE AND \"SORT_ORDER\" >= :sortOrder";
        if (excludeBlockId != null) {
            sql += " AND \"BLOCK_ID\" <> :blockId";
        }
        var q = em.createNativeQuery(sql)
            .setParameter("documentId", documentId)
            .setParameter("versionId", versionId)
            .setParameter("sortOrder", sortOrder);
        if (excludeBlockId != null) {
            q.setParameter("blockId", excludeBlockId);
        }
        q.executeUpdate();
    }

    private void shiftBlockSortOrderDown(long documentId, int sortOrder) {
        em.createNativeQuery(
            "UPDATE \"TB_APPLICATION_DOCUMENT_BLOCK\" " +
            "SET \"SORT_ORDER\" = \"SORT_ORDER\" - 1 " +
            "WHERE \"DOCUMENT_ID\" = :documentId AND \"IS_DELETED\" = FALSE AND \"SORT_ORDER\" > :sortOrder"
        )
        .setParameter("documentId", documentId)
        .setParameter("sortOrder", sortOrder)
        .executeUpdate();
    }

    private void shiftBlockVersionSortOrderDown(long documentId, int versionId, int sortOrder) {
        em.createNativeQuery(
            "UPDATE \"TB_APPLICATION_DOCUMENT_BLOCK_VERSION\" " +
            "SET \"SORT_ORDER\" = \"SORT_ORDER\" - 1 " +
            "WHERE \"DOCUMENT_ID\" = :documentId AND \"VERSION_ID\" = :versionId " +
            "AND \"IS_DELETED\" = FALSE AND \"SORT_ORDER\" > :sortOrder"
        )
        .setParameter("documentId", documentId)
        .setParameter("versionId", versionId)
        .setParameter("sortOrder", sortOrder)
        .executeUpdate();
    }

    private void copyBlockVersions(long documentId,
            int fromVersionId,
            int toVersionId,
            long createdBy,
            OffsetDateTime createdAt) {
        em.createNativeQuery(
            "INSERT INTO \"TB_APPLICATION_DOCUMENT_BLOCK_VERSION\" " +
            "(\"BLOCK_ID\",\"DOCUMENT_ID\",\"VERSION_ID\",\"PARENT_BLOCK_ID\",\"BLOCK_TYPE\",\"BLOCK_DATA\",\"SORT_ORDER\",\"IS_DELETED\",\"CREATED_BY\",\"CREATED_AT\") " +
            "SELECT \"BLOCK_ID\",\"DOCUMENT_ID\",:toVersionId,\"PARENT_BLOCK_ID\",\"BLOCK_TYPE\",\"BLOCK_DATA\",\"SORT_ORDER\",\"IS_DELETED\",:createdBy,:createdAt " +
            "FROM \"TB_APPLICATION_DOCUMENT_BLOCK_VERSION\" " +
            "WHERE \"DOCUMENT_ID\" = :documentId AND \"VERSION_ID\" = :fromVersionId"
        )
        .setParameter("toVersionId", toVersionId)
        .setParameter("createdBy", createdBy)
        .setParameter("createdAt", createdAt)
        .setParameter("documentId", documentId)
        .setParameter("fromVersionId", fromVersionId)
        .executeUpdate();
    }

    private DocumentVersion toDomain(DocumentVersionEntity e) {
        return new DocumentVersion(
            e.getId().getDocumentId(),
            e.getId().getVersionId(),
            e.getState(),
            e.getTitle(),
            e.getSecured() != null && e.getSecured(),
            e.getContentType(),
            e.getTemplate(),
            e.getScript(),
            e.getPattern(),
            e.getSummary(),
            e.getCreatedBy(),
            e.getCreatedAt(),
            e.getUpdatedBy(),
            e.getUpdatedAt()
        );
    }
}
