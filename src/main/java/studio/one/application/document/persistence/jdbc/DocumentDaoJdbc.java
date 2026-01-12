package studio.one.application.document.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
import studio.one.application.document.domain.model.DocumentBlock;
import studio.one.application.document.domain.exception.BlockConflictException;
import studio.one.application.document.domain.exception.DocumentConflictException;
import studio.one.application.document.domain.model.Document;
import studio.one.application.document.domain.model.DocumentSummary;
import studio.one.application.document.domain.model.DocumentVersion;
import studio.one.application.document.domain.model.DocumentVersionBundle;
import studio.one.application.document.domain.exception.DocumentNotFoundException;
import studio.one.platform.data.sqlquery.annotation.SqlBoundStatement;
import studio.one.platform.data.sqlquery.annotation.SqlStatement;
import studio.one.platform.data.sqlquery.mapping.BoundSql;

public class DocumentDaoJdbc implements DocumentDao {

    private final NamedParameterJdbcTemplate jdbc;

    @SqlStatement("data.document.insert")
    private String insertDocumentSql;

    @SqlStatement("data.document.insertVersion")
    private String insertVersionSql;

    @SqlStatement("data.document.insertBody")
    private String insertBodySql;

    @SqlStatement("data.document.insertBodyVersion")
    private String insertBodyVersionSql;

    @SqlStatement("data.document.insertProperty")
    private String insertPropertySql;

    @SqlStatement("data.document.selectCurrentVersionForUpdate")
    private String selectCurrentVersionForUpdateSql;

    @SqlStatement("data.document.updateCurrentVersion")
    private String updateCurrentVersionSql;

    @SqlStatement("data.document.touchDocument")
    private String touchDocumentSql;

    @SqlStatement("data.document.selectDocument")
    private String selectDocumentSql;

    @SqlStatement("data.document.selectVersion")
    private String selectVersionSql;

    @SqlStatement("data.document.selectProperties")
    private String selectPropertiesSql;

    @SqlStatement("data.document.selectVersions")
    private String selectVersionsSql;

    @SqlStatement("data.document.selectDocuments")
    private String selectDocumentsSql;

    @SqlStatement("data.document.countDocuments")
    private String countDocumentsSql;

    @SqlStatement("data.document.selectDocumentSummaries")
    private String selectDocumentSummariesSql;

    @SqlStatement("data.document.selectDocumentsByObject")
    private String selectDocumentsByObjectSql;

    @SqlStatement("data.document.countDocumentsByObject")
    private String countDocumentsByObjectSql;

    @SqlStatement("data.document.selectDocumentSummariesByObject")
    private String selectDocumentSummariesByObjectSql;

    @SqlStatement("data.document.selectDocumentsByNameOrBody")
    private String selectDocumentsByNameOrBodySql;

    @SqlStatement("data.document.countDocumentsByNameOrBody")
    private String countDocumentsByNameOrBodySql;

    @SqlStatement("data.document.selectDocumentSummariesByNameOrBody")
    private String selectDocumentSummariesByNameOrBodySql;

    @SqlStatement("data.document.selectDocumentsByParent")
    private String selectDocumentsByParentSql;

    @SqlStatement("data.document.countDocumentsByParent")
    private String countDocumentsByParentSql;

    @SqlStatement("data.document.selectDocumentSummariesByParent")
    private String selectDocumentSummariesByParentSql;

    @SqlStatement("data.document.selectRootDocuments")
    private String selectRootDocumentsSql;

    @SqlStatement("data.document.countRootDocuments")
    private String countRootDocumentsSql;

    @SqlStatement("data.document.selectRootDocumentSummaries")
    private String selectRootDocumentSummariesSql;

    @SqlStatement("data.document.updateMeta")
    private String updateMetaSql;

    @SqlStatement("data.document.deleteDocument")
    private String deleteDocumentSql;

    @SqlStatement("data.document.deleteDocumentBlockVersions")
    private String deleteDocumentBlockVersionsSql;

    @SqlStatement("data.document.deleteDocumentBlocks")
    private String deleteDocumentBlocksSql;

    @SqlStatement("data.document.deleteDocumentBodyVersions")
    private String deleteDocumentBodyVersionsSql;

    @SqlStatement("data.document.deleteDocumentBodies")
    private String deleteDocumentBodiesSql;

    @SqlStatement("data.document.deleteDocumentProperties")
    private String deleteDocumentPropertiesSql;

    @SqlStatement("data.document.deleteDocumentVersions")
    private String deleteDocumentVersionsSql;

    @SqlBoundStatement("data.document.selectBodyLatest")
    private BoundSql selectBodyLatestSql;

    @SqlStatement("data.document.selectDocumentUpdatedAtForUpdate")
    private String selectDocumentUpdatedAtForUpdateSql;

    @SqlStatement("data.document.selectCurrentVersion")
    private String selectCurrentVersionSql;

    @SqlStatement("data.document.insertBlock")
    private String insertBlockSql;

    @SqlStatement("data.document.updateBlock")
    private String updateBlockSql;

    @SqlStatement("data.document.updateBlockDeleted")
    private String updateBlockDeletedSql;

    @SqlStatement("data.document.deleteBlock")
    private String deleteBlockSql;

    @SqlStatement("data.document.selectBlocksByDocument")
    private String selectBlocksByDocumentSql;

    @SqlStatement("data.document.selectBlocksByVersion")
    private String selectBlocksByVersionSql;

    @SqlStatement("data.document.selectBlocksByVersionAll")
    private String selectBlocksByVersionAllSql;

    @SqlStatement("data.document.insertBlockVersion")
    private String insertBlockVersionSql;

    @SqlStatement("data.document.selectBlockVersion")
    private String selectBlockVersionSql;

    @SqlStatement("data.document.deleteBlockVersion")
    private String deleteBlockVersionSql;

    @SqlStatement("data.document.selectBlockDocumentId")
    private String selectBlockDocumentIdSql;

    @SqlStatement("data.document.selectBlockMeta")
    private String selectBlockMetaSql;

    @SqlStatement("data.document.selectMaxBlockSortOrder")
    private String selectMaxBlockSortOrderSql;

    @SqlStatement("data.document.shiftBlockSortOrder")
    private String shiftBlockSortOrderSql;

    @SqlStatement("data.document.shiftBlockVersionSortOrder")
    private String shiftBlockVersionSortOrderSql;

    @SqlStatement("data.document.shiftBlockSortOrderDown")
    private String shiftBlockSortOrderDownSql;

    @SqlStatement("data.document.shiftBlockVersionSortOrderDown")
    private String shiftBlockVersionSortOrderDownSql;

    @SqlStatement("data.document.copyBlockVersion")
    private String copyBlockVersionSql;

    public DocumentDaoJdbc(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Timestamp ts(OffsetDateTime t) {
        return t == null ? null : Timestamp.from(t.toInstant());
    }

    private void touchDocument(long documentId, long actorUserId, OffsetDateTime now) {
        jdbc.update(
                touchDocumentSql,
                Map.of(
                        "documentId", documentId,
                        "updatedBy", actorUserId,
                        "updatedAt", ts(now)));
    }

    private final RowMapper<Document> documentMapper = new RowMapper<Document>() {
        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document d = new Document();
            d.setDocumentId(rs.getLong("DOCUMENT_ID"));
            d.setObjectType(rs.getInt("OBJECT_TYPE"));
            d.setObjectId(rs.getLong("OBJECT_ID"));
            Object pd = rs.getObject("PARENT_DOCUMENT_ID");
            d.setParentDocumentId(pd == null ? null : ((Number) pd).longValue());
            Object so = rs.getObject("SORT_ORDER");
            d.setSortOrder(so == null ? null : ((Number) so).intValue());
            d.setName(rs.getString("NAME"));
            d.setCurrentVersionId(rs.getInt("VERSION_ID"));
            d.setReadCount(rs.getLong("READ_COUNT"));
            d.setPattern(rs.getString("PATTERN"));
            d.setCreatedBy(rs.getLong("CREATED_BY"));
            d.setCreatedAt(rs.getObject("CREATED_AT", java.time.OffsetDateTime.class));
            Object ub = rs.getObject("UPDATED_BY");
            d.setUpdatedBy(ub == null ? null : ((Number) ub).longValue());
            d.setUpdatedAt(rs.getObject("UPDATED_AT", java.time.OffsetDateTime.class));
            return d;
        }
    };

    private final RowMapper<DocumentSummary> documentSummaryMapper = new RowMapper<DocumentSummary>() {
        @Override
        public DocumentSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DocumentSummary(
                    rs.getLong("DOCUMENT_ID"),
                    rs.getObject("OBJECT_TYPE", Integer.class),
                    rs.getObject("OBJECT_ID", Long.class),
                    rs.getObject("PARENT_DOCUMENT_ID", Long.class),
                    rs.getObject("SORT_ORDER", Integer.class),
                    rs.getString("NAME"),
                    rs.getString("LATEST_TITLE"),
                    rs.getObject("VERSION_ID", Integer.class),
                    rs.getLong("CREATED_BY"),
                    rs.getObject("UPDATED_BY", Long.class),
                    rs.getObject("CREATED_AT", OffsetDateTime.class),
                    rs.getObject("UPDATED_AT", OffsetDateTime.class)
            );
        }
    };

    private final RowMapper<DocumentVersion> versionMapper = new RowMapper<DocumentVersion>() {
        @Override
        public DocumentVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentVersion v = new DocumentVersion();
            v.setDocumentId(rs.getLong("DOCUMENT_ID"));
            v.setVersionId(rs.getInt("VERSION_ID"));
            v.setState(rs.getString("STATE"));
            v.setTitle(rs.getString("TITLE"));
            v.setSecured(rs.getBoolean("SECURED"));
            v.setContentType(rs.getString("CONTENT_TYPE"));
            v.setTemplate(rs.getString("TEMPLATE"));
            v.setScript(rs.getString("SCRIPT"));
            v.setPattern(rs.getString("PATTERN"));
            v.setSummary(rs.getString("SUMMARY"));
            v.setCreatedBy(rs.getLong("CREATED_BY"));
            v.setCreatedAt(rs.getObject("CREATED_AT", java.time.OffsetDateTime.class));
            Object ub = rs.getObject("UPDATED_BY");
            v.setUpdatedBy(ub == null ? null : ((Number) ub).longValue());
            v.setUpdatedAt(rs.getObject("UPDATED_AT", java.time.OffsetDateTime.class));
            return v;
        }
    };

    private final RowMapper<DocumentBlock> blockMapper = new RowMapper<DocumentBlock>() {
        @Override
        public DocumentBlock mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentBlock b = new DocumentBlock();
            b.setBlockId(rs.getLong("BLOCK_ID"));
            b.setDocumentId(rs.getLong("DOCUMENT_ID"));
            Object pb = rs.getObject("PARENT_BLOCK_ID");
            b.setParentBlockId(pb == null ? null : ((Number) pb).longValue());
            b.setBlockType(rs.getString("BLOCK_TYPE"));
            b.setBlockData(rs.getString("BLOCK_DATA"));
            Object so = rs.getObject("SORT_ORDER");
            b.setSortOrder(so == null ? null : ((Number) so).intValue());
            b.setDeleted(rs.getObject("IS_DELETED") != null && rs.getBoolean("IS_DELETED"));
            b.setCreatedBy(rs.getLong("CREATED_BY"));
            b.setCreatedAt(rs.getObject("CREATED_AT", java.time.OffsetDateTime.class));
            Object ub = rs.getObject("UPDATED_BY");
            b.setUpdatedBy(ub == null ? null : ((Number) ub).longValue());
            b.setUpdatedAt(rs.getObject("UPDATED_AT", java.time.OffsetDateTime.class));
            return b;
        }
    };

    @Override
    @Transactional
    public long createDocument(CreateDocumentCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();

        KeyHolder docKh = new GeneratedKeyHolder();
        jdbc.update(
                insertDocumentSql,
                new MapSqlParameterSource()
                        .addValue("objectType", cmd.getObjectType())
                        .addValue("objectId", cmd.getObjectId())
                        .addValue("parentDocumentId", cmd.getParentDocumentId())
                        .addValue("sortOrder", cmd.getSortOrder() == null ? 0 : cmd.getSortOrder())
                        .addValue("name", cmd.getName())
                        .addValue("pattern", null)
                        .addValue("createdBy", cmd.getActorUserId())
                        .addValue("createdAt", ts(now)),
                docKh,
                new String[] { "DOCUMENT_ID" });

        long documentId = Objects.requireNonNull(docKh.getKey()).longValue();

        jdbc.update(insertVersionSql, Map.of(
                "documentId", documentId,
                "versionId", 1,
                "title", cmd.getTitle(),
                "createdBy", cmd.getActorUserId(),
                "createdAt", ts(now)));

        KeyHolder bodyKh = new GeneratedKeyHolder();
        jdbc.update(
                insertBodySql,
                new MapSqlParameterSource()
                        .addValue("documentId", documentId)
                        .addValue("bodyType", cmd.getBodyType())
                        .addValue("bodyText", cmd.getBodyText())
                        .addValue("createdBy", cmd.getActorUserId())
                        .addValue("createdAt", ts(now)),
                bodyKh,
                new String[] { "BODY_ID" });
        long bodyId = Objects.requireNonNull(bodyKh.getKey()).longValue();

        jdbc.update(insertBodyVersionSql, Map.of(
                "bodyId", bodyId,
                "documentId", documentId,
                "versionId", 1,
                "createdBy", cmd.getActorUserId(),
                "createdAt", ts(now)));

        Map<String, String> props = cmd.getProperties();
        if (props != null && !props.isEmpty()) {
            for (var e : props.entrySet()) {
                jdbc.update(insertPropertySql, Map.of(
                        "documentId", documentId,
                        "versionId", 1,
                        "name", e.getKey(),
                        "value", e.getValue(),
                        "createdBy", cmd.getActorUserId(),
                        "createdAt", ts(now)));
            }
        }

        return documentId;
    }

    @Override
    @Transactional
    public int createNewVersion(long documentId, CreateVersionCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();

        Integer current = jdbc.queryForObject(
                selectCurrentVersionForUpdateSql,
                Map.of("documentId", documentId),
                Integer.class);
        if (current == null)
            throw DocumentNotFoundException.byId(documentId);
        int newVersion = current + 1;

        jdbc.update(insertVersionSql, Map.of(
                "documentId", documentId,
                "versionId", newVersion,
                "title", cmd.getTitle(),
                "createdBy", cmd.getActorUserId(),
                "createdAt", ts(now)));

        KeyHolder bodyKh = new GeneratedKeyHolder();
        jdbc.update(
                insertBodySql,
                new MapSqlParameterSource()
                        .addValue("documentId", documentId)
                        .addValue("bodyType", cmd.getBodyType())
                        .addValue("bodyText", cmd.getBodyText())
                        .addValue("createdBy", cmd.getActorUserId())
                        .addValue("createdAt", ts(now)),
                bodyKh,
                new String[] { "BODY_ID" });
        long bodyId = Objects.requireNonNull(bodyKh.getKey()).longValue();

        jdbc.update(insertBodyVersionSql, Map.of(
                "bodyId", bodyId,
                "documentId", documentId,
                "versionId", newVersion,
                "createdBy", cmd.getActorUserId(),
                "createdAt", ts(now)));

        Map<String, String> props = cmd.getProperties();
        if (props != null && !props.isEmpty()) {
            for (var e : props.entrySet()) {
                jdbc.update(insertPropertySql, Map.of(
                        "documentId", documentId,
                        "versionId", newVersion,
                        "name", e.getKey(),
                        "value", e.getValue(),
                        "createdBy", cmd.getActorUserId(),
                        "createdAt", ts(now)));
            }
        }

        jdbc.update(
                updateCurrentVersionSql,
                Map.of(
                        "versionId", newVersion,
                        "updatedBy", cmd.getActorUserId(),
                        "updatedAt", ts(now),
                        "documentId", documentId));

        jdbc.update(
                copyBlockVersionSql,
                Map.of(
                        "documentId", documentId,
                        "fromVersionId", current,
                        "toVersionId", newVersion,
                        "createdBy", cmd.getActorUserId(),
                        "createdAt", ts(now)));

        return newVersion;
    }

    @Override
    public Optional<Document> findDocument(long documentId) {
        List<Document> rows = jdbc.query(
                selectDocumentSql,
                Map.of("documentId", documentId),
                documentMapper);
        return rows.stream().findFirst();
    }

    @Override
    public Optional<DocumentVersion> findVersion(long documentId, int versionId) {
        List<DocumentVersion> rows = jdbc.query(
                selectVersionSql,
                Map.of("documentId", documentId, "versionId", versionId),
                versionMapper);
        return rows.stream().findFirst();
    }

    @Override
    public Optional<DocumentVersionBundle> findVersionBundle(long documentId, int versionId) {
        Optional<Document> doc = findDocument(documentId);
        Optional<DocumentVersion> ver = findVersion(documentId, versionId);
        if (doc.isEmpty() || ver.isEmpty())
            return Optional.empty();

        List<Map.Entry<Integer, String>> bodyRows = jdbc.query(
                selectBodyLatestSql.getSql(),
                Map.of("documentId", documentId, "versionId", versionId),
                (rs, rn) -> Map.entry(rs.getInt("BODY_TYPE"), rs.getString("BODY_TEXT")));

        Integer bodyType = bodyRows.isEmpty() ? null : bodyRows.get(0).getKey();
        String bodyText = bodyRows.isEmpty() ? null : bodyRows.get(0).getValue();

        Map<String, String> props = new LinkedHashMap<>();
        jdbc.query(
                selectPropertiesSql,
                Map.of("documentId", documentId, "versionId", versionId),
                (RowCallbackHandler) rs -> props.put(rs.getString("PROPERTY_NAME"), rs.getString("PROPERTY_VALUE")));

        return Optional.of(new DocumentVersionBundle(doc.get(), ver.get(), bodyType, bodyText, props));
    }

    @Override
    public Optional<DocumentVersionBundle> findLatestBundle(long documentId) {
        Optional<Document> doc = findDocument(documentId);
        if (doc.isEmpty())
            return Optional.empty();
        return findVersionBundle(documentId, doc.get().getCurrentVersionId());
    }

    @Override
    public List<DocumentVersion> listVersions(long documentId) {
        return jdbc.query(
                selectVersionsSql,
                Map.of("documentId", documentId),
                versionMapper);
    }

    @Override
    public Page<Document> findAll(Pageable pageable) {
        return queryDocumentPage(selectDocumentsSql, countDocumentsSql, new MapSqlParameterSource(), pageable);
    }

    @Override
    public Page<Document> findByNameOrBody(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(pageable);
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("keyword", "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%");
        return queryDocumentPage(selectDocumentsByNameOrBodySql, countDocumentsByNameOrBodySql, params, pageable);
    }

    @Override
    public Page<Document> findByObjectTypeAndObjectId(int objectType, long objectId, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("objectType", objectType)
                .addValue("objectId", objectId);
        return queryDocumentPage(selectDocumentsByObjectSql, countDocumentsByObjectSql, params, pageable);
    }

    @Override
    public Page<Document> findByParentDocumentId(Long parentDocumentId, Pageable pageable) {
        if (parentDocumentId == null) {
            return queryDocumentPage(selectRootDocumentsSql, countRootDocumentsSql, new MapSqlParameterSource(), pageable);
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentDocumentId", parentDocumentId);
        return queryDocumentPage(selectDocumentsByParentSql, countDocumentsByParentSql, params, pageable);
    }

    @Override
    public Page<DocumentSummary> findSummaryAll(Pageable pageable) {
        return queryDocumentSummaryPage(selectDocumentSummariesSql, countDocumentsSql, new MapSqlParameterSource(), pageable);
    }

    @Override
    public Page<DocumentSummary> findSummaryByNameOrBody(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findSummaryAll(pageable);
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("keyword", "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%");
        return queryDocumentSummaryPage(selectDocumentSummariesByNameOrBodySql, countDocumentsByNameOrBodySql, params, pageable);
    }

    @Override
    public Page<DocumentSummary> findSummaryByObjectTypeAndObjectId(int objectType, long objectId, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("objectType", objectType)
                .addValue("objectId", objectId);
        return queryDocumentSummaryPage(selectDocumentSummariesByObjectSql, countDocumentsByObjectSql, params, pageable);
    }

    @Override
    public Page<DocumentSummary> findSummaryByParentDocumentId(Long parentDocumentId, Pageable pageable) {
        if (parentDocumentId == null) {
            return queryDocumentSummaryPage(selectRootDocumentSummariesSql, countRootDocumentsSql, new MapSqlParameterSource(), pageable);
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentDocumentId", parentDocumentId);
        return queryDocumentSummaryPage(selectDocumentSummariesByParentSql, countDocumentsByParentSql, params, pageable);
    }

    @Override
    @Transactional
    public void updateDocumentMeta(UpdateDocumentMetaCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime currentUpdatedAt = jdbc.queryForObject(
                selectDocumentUpdatedAtForUpdateSql,
                Map.of("documentId", cmd.getDocumentId()),
                (rs, rn) -> rs.getObject("UPDATED_AT", OffsetDateTime.class));
        assertNotStaleDocument(cmd.getExpectedUpdatedAt(), currentUpdatedAt, cmd.getDocumentId());
        jdbc.update(
                updateMetaSql,
                new MapSqlParameterSource()
                        .addValue("name", cmd.getName())
                        .addValue("pattern", cmd.getPattern())
                        .addValue("updatedBy", cmd.getActorUserId())
                        .addValue("updatedAt", ts(now))
                        .addValue("documentId", cmd.getDocumentId()));
    }

    @Override
    @Transactional
    public void deleteDocument(DeleteDocumentCommand cmd) {
        OffsetDateTime currentUpdatedAt = jdbc.queryForObject(
                selectDocumentUpdatedAtForUpdateSql,
                Map.of("documentId", cmd.getDocumentId()),
                (rs, rn) -> rs.getObject("UPDATED_AT", OffsetDateTime.class));
        assertNotStaleDocument(cmd.getExpectedUpdatedAt(), currentUpdatedAt, cmd.getDocumentId());
        Map<String, Object> params = Map.of("documentId", cmd.getDocumentId());
        jdbc.update(deleteDocumentBlockVersionsSql, params);
        jdbc.update(deleteDocumentBlocksSql, params);
        jdbc.update(deleteDocumentBodyVersionsSql, params);
        jdbc.update(deleteDocumentBodiesSql, params);
        jdbc.update(deleteDocumentPropertiesSql, params);
        jdbc.update(deleteDocumentVersionsSql, params);
        jdbc.update(deleteDocumentSql, params);
    }

    @Override
    @Transactional
    public long createBlock(CreateBlockCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        Integer versionId = jdbc.queryForObject(
                selectCurrentVersionForUpdateSql,
                Map.of("documentId", cmd.getDocumentId()),
                Integer.class);
        assertParentBlock(cmd.getDocumentId(), cmd.getParentBlockId(), null);
        int sortOrder = resolveBlockSortOrder(cmd.getDocumentId(), versionId, cmd.getSortOrder());
        if (versionId != null) {
            jdbc.update(
                    shiftBlockSortOrderSql,
                    Map.of(
                            "documentId", cmd.getDocumentId(),
                            "sortOrder", sortOrder));
            jdbc.update(
                    shiftBlockVersionSortOrderSql,
                    Map.of(
                            "documentId", cmd.getDocumentId(),
                            "versionId", versionId,
                            "sortOrder", sortOrder,
                            "blockId", -1));
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                insertBlockSql,
                new MapSqlParameterSource()
                        .addValue("documentId", cmd.getDocumentId())
                        .addValue("parentBlockId", cmd.getParentBlockId())
                        .addValue("blockType", cmd.getBlockType())
                        .addValue("blockData", cmd.getBlockData())
                        .addValue("sortOrder", sortOrder)
                        .addValue("isDeleted", false)
                        .addValue("createdBy", cmd.getActorUserId())
                        .addValue("createdAt", ts(now)),
                keyHolder,
                new String[] { "BLOCK_ID" });
        long blockId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (versionId != null) {
            jdbc.update(
                    insertBlockVersionSql,
                    new MapSqlParameterSource()
                            .addValue("blockId", blockId)
                            .addValue("documentId", cmd.getDocumentId())
                            .addValue("versionId", versionId)
                            .addValue("parentBlockId", cmd.getParentBlockId())
                            .addValue("blockType", cmd.getBlockType())
                            .addValue("blockData", cmd.getBlockData())
                            .addValue("sortOrder", sortOrder)
                            .addValue("isDeleted", false)
                            .addValue("createdBy", cmd.getActorUserId())
                            .addValue("createdAt", ts(now)));
        }
        touchDocument(cmd.getDocumentId(), cmd.getActorUserId(), now);
        return blockId;
    }

    @Override
    @Transactional
    public void updateBlock(UpdateBlockCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        Map<String, Object> meta = null;
        try {
            meta = jdbc.queryForMap(
                    selectBlockMetaSql,
                    Map.of("blockId", cmd.getBlockId()));
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            meta = null;
        }
        if (meta == null) {
            return;
        }
        Long documentId = meta.get("DOCUMENT_ID") == null ? null : ((Number) meta.get("DOCUMENT_ID")).longValue();
        Integer currentSortOrder = meta.get("SORT_ORDER") == null ? null : ((Number) meta.get("SORT_ORDER")).intValue();
        Long currentParentBlockId = meta.get("PARENT_BLOCK_ID") == null ? null : ((Number) meta.get("PARENT_BLOCK_ID")).longValue();
        String currentBlockType = meta.get("BLOCK_TYPE") == null ? null : meta.get("BLOCK_TYPE").toString();
        String currentBlockData = meta.get("BLOCK_DATA") == null ? null : meta.get("BLOCK_DATA").toString();
        OffsetDateTime currentUpdatedAt = readUpdatedAt(meta.get("UPDATED_AT"));
        assertNotStale(cmd.getExpectedUpdatedAt(), currentUpdatedAt, cmd.getBlockId());
        assertParentBlock(documentId, cmd.getParentBlockId(), cmd.getBlockId());
        if (documentId == null) {
            return;
        }
        Integer versionId = jdbc.queryForObject(
                selectCurrentVersionForUpdateSql,
                Map.of("documentId", documentId),
                Integer.class);
        int nextSortOrder = cmd.getSortOrder() == null && currentSortOrder != null
                ? currentSortOrder
                : resolveBlockSortOrder(documentId, versionId, cmd.getSortOrder());
        if (Objects.equals(cmd.getParentBlockId(), currentParentBlockId)
                && Objects.equals(currentSortOrder, nextSortOrder)
                && Objects.equals(cmd.getBlockType(), currentBlockType)
                && Objects.equals(cmd.getBlockData(), currentBlockData)) {
            return;
        }
        if (versionId != null && currentSortOrder != null && nextSortOrder != currentSortOrder) {
            jdbc.update(
                    shiftBlockSortOrderSql,
                    Map.of(
                            "documentId", documentId,
                            "sortOrder", nextSortOrder));
            jdbc.update(
                    shiftBlockVersionSortOrderSql,
                    Map.of(
                            "documentId", documentId,
                            "versionId", versionId,
                            "sortOrder", nextSortOrder,
                            "blockId", cmd.getBlockId()));
        }
        jdbc.update(
                updateBlockSql,
                new MapSqlParameterSource()
                        .addValue("blockId", cmd.getBlockId())
                        .addValue("parentBlockId", cmd.getParentBlockId())
                        .addValue("blockType", cmd.getBlockType())
                        .addValue("blockData", cmd.getBlockData())
                        .addValue("sortOrder", nextSortOrder)
                        .addValue("updatedBy", cmd.getActorUserId())
                        .addValue("updatedAt", ts(now)));
        touchDocument(documentId, cmd.getActorUserId(), now);
        if (versionId == null) {
            return;
        }
        Map<String, Object> ver = null;
        try {
            ver = jdbc.queryForMap(
                    selectBlockVersionSql,
                    Map.of(
                            "blockId", cmd.getBlockId(),
                            "documentId", documentId,
                            "versionId", versionId));
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            ver = null;
        }
        if (isSameBlockVersion(ver, cmd.getParentBlockId(), cmd.getBlockType(), cmd.getBlockData(), nextSortOrder, false)) {
            return;
        }
        jdbc.update(
                deleteBlockVersionSql,
                Map.of(
                        "blockId", cmd.getBlockId(),
                        "documentId", documentId,
                        "versionId", versionId));
        jdbc.update(
                insertBlockVersionSql,
                Map.of(
                        "blockId", cmd.getBlockId(),
                        "documentId", documentId,
                        "versionId", versionId,
                        "parentBlockId", cmd.getParentBlockId(),
                        "blockType", cmd.getBlockType(),
                        "blockData", cmd.getBlockData(),
                        "sortOrder", nextSortOrder,
                        "isDeleted", false,
                        "createdBy", cmd.getActorUserId(),
                        "createdAt", ts(now)));
    }

    @Override
    @Transactional
    public void moveBlock(MoveBlockCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        Map<String, Object> meta = null;
        try {
            meta = jdbc.queryForMap(
                    selectBlockMetaSql,
                    Map.of("blockId", cmd.getBlockId()));
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            meta = null;
        }
        if (meta == null) {
            return;
        }
        Long documentId = meta.get("DOCUMENT_ID") == null ? null : ((Number) meta.get("DOCUMENT_ID")).longValue();
        Integer currentSortOrder = meta.get("SORT_ORDER") == null ? null : ((Number) meta.get("SORT_ORDER")).intValue();
        Long currentParentBlockId = meta.get("PARENT_BLOCK_ID") == null ? null : ((Number) meta.get("PARENT_BLOCK_ID")).longValue();
        OffsetDateTime currentUpdatedAt = readUpdatedAt(meta.get("UPDATED_AT"));
        assertNotStale(cmd.getExpectedUpdatedAt(), currentUpdatedAt, cmd.getBlockId());
        assertParentBlock(documentId, cmd.getParentBlockId(), cmd.getBlockId());
        if (documentId == null) {
            return;
        }
        Integer versionId = jdbc.queryForObject(
                selectCurrentVersionForUpdateSql,
                Map.of("documentId", documentId),
                Integer.class);
        int nextSortOrder = cmd.getSortOrder() == null && currentSortOrder != null
                ? currentSortOrder
                : resolveBlockSortOrder(documentId, versionId, cmd.getSortOrder());
        if (Objects.equals(cmd.getParentBlockId(), currentParentBlockId)
                && Objects.equals(currentSortOrder, nextSortOrder)) {
            return;
        }
        if (versionId != null && currentSortOrder != null && nextSortOrder != currentSortOrder) {
            jdbc.update(
                    shiftBlockSortOrderSql,
                    Map.of(
                            "documentId", documentId,
                            "sortOrder", nextSortOrder));
            jdbc.update(
                    shiftBlockVersionSortOrderSql,
                    Map.of(
                            "documentId", documentId,
                            "versionId", versionId,
                            "sortOrder", nextSortOrder,
                            "blockId", cmd.getBlockId()));
        }
        jdbc.update(
                updateBlockSql,
                new MapSqlParameterSource()
                        .addValue("blockId", cmd.getBlockId())
                        .addValue("parentBlockId", cmd.getParentBlockId())
                        .addValue("blockType", meta.get("BLOCK_TYPE"))
                        .addValue("blockData", meta.get("BLOCK_DATA"))
                        .addValue("sortOrder", nextSortOrder)
                        .addValue("updatedBy", cmd.getActorUserId())
                        .addValue("updatedAt", ts(now)));
        touchDocument(documentId, cmd.getActorUserId(), now);
        if (versionId == null) {
            return;
        }
        Map<String, Object> ver = null;
        try {
            ver = jdbc.queryForMap(
                    selectBlockVersionSql,
                    Map.of(
                            "blockId", cmd.getBlockId(),
                            "documentId", documentId,
                            "versionId", versionId));
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            ver = null;
        }
        String blockType = meta.get("BLOCK_TYPE") == null ? null : meta.get("BLOCK_TYPE").toString();
        String blockData = meta.get("BLOCK_DATA") == null ? null : meta.get("BLOCK_DATA").toString();
        if (isSameBlockVersion(ver, cmd.getParentBlockId(), blockType, blockData, nextSortOrder, false)) {
            return;
        }
        jdbc.update(
                deleteBlockVersionSql,
                Map.of(
                        "blockId", cmd.getBlockId(),
                        "documentId", documentId,
                        "versionId", versionId));
        jdbc.update(
                insertBlockVersionSql,
                Map.of(
                        "blockId", cmd.getBlockId(),
                        "documentId", documentId,
                        "versionId", versionId,
                        "parentBlockId", cmd.getParentBlockId(),
                        "blockType", blockType,
                        "blockData", blockData,
                        "sortOrder", nextSortOrder,
                        "isDeleted", false,
                        "createdBy", cmd.getActorUserId(),
                        "createdAt", ts(now)));
    }

    @Override
    @Transactional
    public void deleteBlock(DeleteBlockCommand cmd) {
        OffsetDateTime now = OffsetDateTime.now();
        long blockId = cmd.getBlockId();
        Map<String, Object> meta = null;
        try {
            meta = jdbc.queryForMap(
                    selectBlockMetaSql,
                    Map.of("blockId", blockId));
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            meta = null;
        }
        if (meta == null) {
            return;
        }
        Long documentId = meta.get("DOCUMENT_ID") == null ? null : ((Number) meta.get("DOCUMENT_ID")).longValue();
        Integer sortOrder = meta.get("SORT_ORDER") == null ? null : ((Number) meta.get("SORT_ORDER")).intValue();
        Long parentBlockId = meta.get("PARENT_BLOCK_ID") == null ? null : ((Number) meta.get("PARENT_BLOCK_ID")).longValue();
        String blockType = meta.get("BLOCK_TYPE") == null ? null : meta.get("BLOCK_TYPE").toString();
        String blockData = meta.get("BLOCK_DATA") == null ? null : meta.get("BLOCK_DATA").toString();
        OffsetDateTime currentUpdatedAt = readUpdatedAt(meta.get("UPDATED_AT"));
        assertNotStale(cmd.getExpectedUpdatedAt(), currentUpdatedAt, blockId);
        if (documentId != null) {
            Integer versionId = jdbc.queryForObject(
                    selectCurrentVersionForUpdateSql,
                    Map.of("documentId", documentId),
                    Integer.class);
            if (versionId != null) {
                jdbc.update(
                        deleteBlockVersionSql,
                        Map.of(
                                "blockId", blockId,
                                "documentId", documentId,
                                "versionId", versionId));
                jdbc.update(
                        insertBlockVersionSql,
                        Map.of(
                                "blockId", blockId,
                                "documentId", documentId,
                                "versionId", versionId,
                                "parentBlockId", parentBlockId,
                                "blockType", blockType,
                                "blockData", blockData,
                                "sortOrder", sortOrder == null ? 0 : sortOrder,
                                "isDeleted", true,
                                "createdBy", cmd.getActorUserId(),
                                "createdAt", ts(now)));
                if (sortOrder != null) {
                    jdbc.update(
                            shiftBlockVersionSortOrderDownSql,
                            Map.of(
                                    "documentId", documentId,
                                    "versionId", versionId,
                                    "sortOrder", sortOrder));
                }
            }
            jdbc.update(
                    updateBlockDeletedSql,
                    new MapSqlParameterSource()
                            .addValue("blockId", blockId)
                            .addValue("updatedBy", cmd.getActorUserId())
                            .addValue("updatedAt", ts(now))
                            .addValue("isDeleted", true));
            touchDocument(documentId, cmd.getActorUserId(), now);
            if (sortOrder != null) {
                jdbc.update(
                        shiftBlockSortOrderDownSql,
                        Map.of(
                                "documentId", documentId,
                                "sortOrder", sortOrder));
            }
        }
    }

    @Override
    public List<DocumentBlock> listBlocks(long documentId) {
        Integer versionId = jdbc.queryForObject(
                selectCurrentVersionSql,
                Map.of("documentId", documentId),
                Integer.class);
        if (versionId == null) {
            return Collections.emptyList();
        }
        return listBlocks(documentId, versionId);
    }

    @Override
    public List<DocumentBlock> listBlocks(long documentId, int versionId) {
        return jdbc.query(
                selectBlocksByVersionSql,
                Map.of("documentId", documentId, "versionId", versionId),
                blockMapper);
    }

    @Override
    public List<DocumentBlock> listBlocksIncludingDeleted(long documentId, int versionId) {
        return jdbc.query(
                selectBlocksByVersionAllSql,
                Map.of("documentId", documentId, "versionId", versionId),
                blockMapper);
    }

    private int resolveBlockSortOrder(long documentId, Integer versionId, Integer desiredSortOrder) {
        if (desiredSortOrder != null) {
            return desiredSortOrder;
        }
        if (versionId == null) {
            return 0;
        }
        Integer max = jdbc.queryForObject(
                selectMaxBlockSortOrderSql,
                Map.of("documentId", documentId, "versionId", versionId),
                Integer.class);
        return (max == null ? -1 : max) + 1;
    }

    private boolean isSameBlockVersion(Map<String, Object> row,
            Long parentBlockId,
            String blockType,
            String blockData,
            Integer sortOrder,
            boolean deleted) {
        if (row == null || row.isEmpty()) {
            return false;
        }
        Long rowParent = row.get("PARENT_BLOCK_ID") == null ? null : ((Number) row.get("PARENT_BLOCK_ID")).longValue();
        String rowType = row.get("BLOCK_TYPE") == null ? null : row.get("BLOCK_TYPE").toString();
        String rowData = row.get("BLOCK_DATA") == null ? null : row.get("BLOCK_DATA").toString();
        Integer rowSort = row.get("SORT_ORDER") == null ? null : ((Number) row.get("SORT_ORDER")).intValue();
        Object rowDeleted = row.get("IS_DELETED");
        boolean rowDel = false;
        if (rowDeleted instanceof Boolean) {
            rowDel = (Boolean) rowDeleted;
        } else if (rowDeleted instanceof Number) {
            rowDel = ((Number) rowDeleted).intValue() != 0;
        } else if (rowDeleted != null) {
            rowDel = Boolean.parseBoolean(rowDeleted.toString());
        }
        return Objects.equals(rowParent, parentBlockId)
                && Objects.equals(rowType, blockType)
                && Objects.equals(rowData, blockData)
                && Objects.equals(rowSort, sortOrder)
                && rowDel == deleted;
    }

    private OffsetDateTime readUpdatedAt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof OffsetDateTime) {
            return (OffsetDateTime) value;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toInstant().atOffset(OffsetDateTime.now().getOffset());
        }
        if (value instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) value).atOffset(OffsetDateTime.now().getOffset());
        }
        return null;
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
        Long parentDocumentId = jdbc.queryForObject(
                selectBlockDocumentIdSql,
                Map.of("blockId", parentBlockId),
                Long.class);
        if (parentDocumentId == null) {
            throw new IllegalArgumentException("parent block not found: " + parentBlockId);
        }
        if (documentId != null && !Objects.equals(documentId, parentDocumentId)) {
            throw new IllegalArgumentException("parent block belongs to another document: " + parentBlockId);
        }
    }

    private Page<Document> queryDocumentPage(String selectSql,
            String countSql,
            MapSqlParameterSource params,
            Pageable pageable) {
        int limit = pageable.isUnpaged() ? Integer.MAX_VALUE : pageable.getPageSize();
        long offset = pageable.isUnpaged() ? 0 : pageable.getOffset();
        MapSqlParameterSource selectParams = new MapSqlParameterSource(params.getValues())
                .addValue("limit", limit)
                .addValue("offset", offset);
        List<Document> rows = jdbc.query(selectSql, selectParams, documentMapper);
        Long total = jdbc.queryForObject(countSql, params, Long.class);
        long totalCount = total == null ? 0 : total;
        return new PageImpl<>(rows, pageable, totalCount);
    }

    private Page<DocumentSummary> queryDocumentSummaryPage(String selectSql,
            String countSql,
            MapSqlParameterSource params,
            Pageable pageable) {
        int limit = pageable.isUnpaged() ? Integer.MAX_VALUE : pageable.getPageSize();
        long offset = pageable.isUnpaged() ? 0 : pageable.getOffset();
        MapSqlParameterSource selectParams = new MapSqlParameterSource(params.getValues())
                .addValue("limit", limit)
                .addValue("offset", offset);
        List<DocumentSummary> rows = jdbc.query(selectSql, selectParams, documentSummaryMapper);
        Long total = jdbc.queryForObject(countSql, params, Long.class);
        long totalCount = total == null ? 0 : total;
        return new PageImpl<>(rows, pageable, totalCount);
    }
}
