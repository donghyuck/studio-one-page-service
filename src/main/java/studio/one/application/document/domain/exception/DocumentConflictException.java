package studio.one.application.document.domain.exception;

public class DocumentConflictException extends RuntimeException {

    private final long documentId;

    public DocumentConflictException(long documentId) {
        super("document update conflict: " + documentId);
        this.documentId = documentId;
    }

    public long getDocumentId() {
        return documentId;
    }
}
