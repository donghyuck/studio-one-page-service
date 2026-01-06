package studio.one.application.document.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import studio.one.application.document.persistence.DocumentDao;
import studio.one.platform.constant.ServiceNames;
import studio.one.application.document.command.CreateDocumentCommand;
import studio.one.application.document.command.CreateVersionCommand;
import studio.one.application.document.command.CreateBlockCommand;
import studio.one.application.document.command.DeleteBlockCommand;
import studio.one.application.document.command.DeleteDocumentCommand;
import studio.one.application.document.command.MoveBlockCommand;
import studio.one.application.document.command.UpdateBlockCommand;
import studio.one.application.document.command.UpdateDocumentMetaCommand;
import studio.one.application.document.domain.model.DocumentBlock;
import studio.one.application.document.domain.model.Document;
import studio.one.application.document.domain.model.DocumentVersionBundle;
import studio.one.application.document.domain.exception.DocumentNotFoundException;

@Service(DocumentService.SERVICE_NAME)
@RequiredArgsConstructor
public class DocumentService {
 
    public static final String SERVICE_NAME = ServiceNames.Featrues.PREFIX + ":document:application-document-service";
    
    private final DocumentDao dao;
 
    @Transactional
    public long create(CreateDocumentCommand cmd) {
        return dao.createDocument(cmd);
    }

    @Transactional
    public int newVersion(long documentId, CreateVersionCommand cmd) {
        return dao.createNewVersion(documentId, cmd);
    }

    public DocumentVersionBundle getLatest(long documentId) {
        return dao.findLatestBundle(documentId)
            .orElseThrow(() -> DocumentNotFoundException.byId(documentId));
    }

    public DocumentVersionBundle getVersion(long documentId, int versionId) {
        return dao.findVersionBundle(documentId, versionId)
            .orElseThrow(() -> new NoSuchElementException("version not found: " + documentId + ":" + versionId));
    }

    @Transactional(readOnly = true)
    public Page<Document> findAll(Pageable pageable) {
        return dao.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Document> findByNameOrBody(String keyword, Pageable pageable) {
        return dao.findByNameOrBody(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Document> findByObjectTypeAndObjectId(int objectType, long objectId, Pageable pageable) {
        return dao.findByObjectTypeAndObjectId(objectType, objectId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Document> findByParentDocumentId(Long parentDocumentId, Pageable pageable) {
        return dao.findByParentDocumentId(parentDocumentId, pageable);
    }

    @Transactional
    public void updateMeta(UpdateDocumentMetaCommand cmd) {
        dao.updateDocumentMeta(cmd);
    }

    @Transactional
    public void delete(DeleteDocumentCommand cmd) {
        dao.deleteDocument(cmd);
    }

    @Transactional
    public long createBlock(CreateBlockCommand cmd) {
        return dao.createBlock(cmd);
    }

    @Transactional
    public void updateBlock(UpdateBlockCommand cmd) {
        dao.updateBlock(cmd);
    }

    @Transactional
    public void moveBlock(MoveBlockCommand cmd) {
        dao.moveBlock(cmd);
    }

    @Transactional
    public void deleteBlock(DeleteBlockCommand cmd) {
        dao.deleteBlock(cmd);
    }

    @Transactional(readOnly = true)
    public List<DocumentBlock> listBlocks(long documentId) {
        return dao.listBlocks(documentId);
    }

    @Transactional(readOnly = true)
    public List<DocumentBlock> listBlocks(long documentId, int versionId) {
        return dao.listBlocks(documentId, versionId);
    }

    @Transactional(readOnly = true)
    public List<DocumentBlock> listBlocksIncludingDeleted(long documentId, int versionId) {
        return dao.listBlocksIncludingDeleted(documentId, versionId);
    }
}
