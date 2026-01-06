package studio.one.application.document.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
import studio.one.application.document.domain.model.DocumentVersion;
import studio.one.application.document.domain.model.DocumentVersionBundle;
import studio.one.platform.constant.ServiceNames;

public interface DocumentDao {
 
    public static final String SERVICE_NAME = ServiceNames.Featrues.PREFIX + ":document:application-document-dao";
    
    long createDocument(CreateDocumentCommand cmd);

    int createNewVersion(long documentId, CreateVersionCommand cmd);

    Optional<Document> findDocument(long documentId);

    Optional<DocumentVersion> findVersion(long documentId, int versionId);

    Optional<DocumentVersionBundle> findVersionBundle(long documentId, int versionId);

    Optional<DocumentVersionBundle> findLatestBundle(long documentId);

    List<DocumentVersion> listVersions(long documentId);

    Page<Document> findAll(Pageable pageable);

    Page<Document> findByNameOrBody(String keyword, Pageable pageable);

    Page<Document> findByObjectTypeAndObjectId(int objectType, long objectId, Pageable pageable);

    Page<Document> findByParentDocumentId(Long parentDocumentId, Pageable pageable);

    void updateDocumentMeta(UpdateDocumentMetaCommand cmd);

    void deleteDocument(DeleteDocumentCommand cmd);

    long createBlock(CreateBlockCommand cmd);

    void updateBlock(UpdateBlockCommand cmd);

    void moveBlock(MoveBlockCommand cmd);

    void deleteBlock(DeleteBlockCommand cmd);

    List<DocumentBlock> listBlocks(long documentId);

    List<DocumentBlock> listBlocks(long documentId, int versionId);

    List<DocumentBlock> listBlocksIncludingDeleted(long documentId, int versionId);
}
