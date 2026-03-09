package studio.one.application.document.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import studio.one.application.document.domain.exception.BlockConflictException;
import studio.one.application.document.domain.exception.DocumentConflictException;

class DocumentExceptionHandlerTest {

    private final DocumentExceptionHandler handler = new DocumentExceptionHandler();

    @Test
    void blockConflictResponseDoesNotLeakInternalIdentifiers() {
        ResponseEntity<Map<String, Object>> response = handler.handleBlockConflict(new BlockConflictException(123L));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("block_conflict", response.getBody().get("error"));
        assertEquals("The block was modified by another request.", response.getBody().get("message"));
        assertFalse(response.getBody().containsKey("blockId"));
    }

    @Test
    void documentConflictResponseDoesNotLeakInternalIdentifiers() {
        ResponseEntity<Map<String, Object>> response = handler.handleDocumentConflict(new DocumentConflictException(456L));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("document_conflict", response.getBody().get("error"));
        assertEquals("The document was modified by another request.", response.getBody().get("message"));
        assertFalse(response.getBody().containsKey("documentId"));
    }

    @Test
    void invalidRequestResponseIsGeneric() {
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidRequest(new NoSuchElementException("block not found: 999"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("invalid_request", response.getBody().get("error"));
        assertEquals("Invalid request.", response.getBody().get("message"));
    }
}
