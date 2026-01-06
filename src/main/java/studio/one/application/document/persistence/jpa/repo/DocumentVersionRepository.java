package studio.one.application.document.persistence.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import studio.one.application.document.persistence.jpa.entity.DocumentVersionEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentVersionId;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersionEntity, DocumentVersionId> {
    List<DocumentVersionEntity> findByIdDocumentIdOrderByIdVersionIdDesc(Long documentId);

    void deleteByIdDocumentId(Long documentId);
}
