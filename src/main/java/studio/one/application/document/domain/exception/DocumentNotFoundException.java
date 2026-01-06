package studio.one.application.document.domain.exception;

import org.springframework.http.HttpStatus;

import studio.one.platform.error.ErrorType;
import studio.one.platform.exception.NotFoundException;

public class DocumentNotFoundException extends NotFoundException {

    private static final ErrorType BY_ID = ErrorType.of("error.document.not.found.id", HttpStatus.NOT_FOUND);
    private static final ErrorType BY_NAME = ErrorType.of("error.document.not.found.name", HttpStatus.NOT_FOUND);

    public DocumentNotFoundException(Long documentId) {
        super(BY_ID, "Document Not Found", documentId);
    }

    public DocumentNotFoundException(String name) {
        super(BY_NAME, "Document Not Found", name);
    }

    public static DocumentNotFoundException byId(Long documentId) {
        return new DocumentNotFoundException(documentId);
    }

    public static DocumentNotFoundException byName(String name) {
        return new DocumentNotFoundException(name);
    }
}
