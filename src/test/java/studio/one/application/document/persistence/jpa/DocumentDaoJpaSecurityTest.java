package studio.one.application.document.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import studio.one.application.document.command.UpdateBlockCommand;
import studio.one.application.document.domain.exception.DocumentNotFoundException;
import studio.one.application.document.persistence.jpa.entity.DocumentBlockEntity;
import studio.one.application.document.persistence.jpa.repo.DocumentBlockRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBlockVersionRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBodyRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBodyVersionRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentPropertyRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentVersionRepository;

class DocumentDaoJpaSecurityTest {

    @Test
    void updateBlockRejectsBlockFromAnotherDocument() {
        DocumentRepository docRepo = mock(DocumentRepository.class);
        DocumentVersionRepository verRepo = mock(DocumentVersionRepository.class);
        DocumentBodyRepository bodyRepo = mock(DocumentBodyRepository.class);
        DocumentBodyVersionRepository bodyVerRepo = mock(DocumentBodyVersionRepository.class);
        DocumentPropertyRepository propRepo = mock(DocumentPropertyRepository.class);
        DocumentBlockRepository blockRepo = mock(DocumentBlockRepository.class);
        DocumentBlockVersionRepository blockVersionRepo = mock(DocumentBlockVersionRepository.class);
        EntityManager em = mock(EntityManager.class);

        DocumentDaoJpa dao = new DocumentDaoJpa(
                docRepo, verRepo, bodyRepo, bodyVerRepo, propRepo, blockRepo, blockVersionRepo, em);

        DocumentBlockEntity block = new DocumentBlockEntity();
        block.setBlockId(200L);
        block.setDocumentId(999L);
        block.setUpdatedAt(OffsetDateTime.now());

        when(blockRepo.findById(200L)).thenReturn(Optional.of(block));

        UpdateBlockCommand cmd = new UpdateBlockCommand(
                100L, 200L, null, "text", "payload", 0, 7L, block.getUpdatedAt());

        assertThrows(DocumentNotFoundException.class, () -> dao.updateBlock(cmd));

        verify(blockRepo).findById(200L);
        verifyNoInteractions(docRepo, verRepo, bodyRepo, bodyVerRepo, propRepo, blockVersionRepo, em);
    }
}
