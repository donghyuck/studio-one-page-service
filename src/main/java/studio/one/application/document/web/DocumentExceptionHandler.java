package studio.one.application.document.web;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import studio.one.application.document.domain.exception.BlockConflictException;
import studio.one.application.document.domain.exception.DocumentConflictException;

@RestControllerAdvice
public class DocumentExceptionHandler {

    @ExceptionHandler(BlockConflictException.class)
    public ResponseEntity<Map<String, Object>> handleBlockConflict(BlockConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of(
                "error", "block_conflict",
                "message", "The block was modified by another request."));
    }

    @ExceptionHandler(DocumentConflictException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentConflict(DocumentConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of(
                "error", "document_conflict",
                "message", "The document was modified by another request."));
    }

    @ExceptionHandler({ IllegalArgumentException.class, NoSuchElementException.class })
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error", "invalid_request",
                "message", "Invalid request."));
    }
}
