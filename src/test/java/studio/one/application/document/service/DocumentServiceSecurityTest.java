package studio.one.application.document.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import studio.one.application.document.domain.exception.DocumentNotFoundException;
import studio.one.application.document.persistence.DocumentDao;

class DocumentServiceSecurityTest {

    @Test
    void getVersionHidesMissingVersionAsDocumentNotFound() {
        DocumentDao dao = mock(DocumentDao.class);
        when(dao.findVersionBundle(100L, 3)).thenReturn(Optional.empty());

        DocumentService service = new DocumentService(dao);

        assertThrows(DocumentNotFoundException.class, () -> service.getVersion(100L, 3));
    }
}
