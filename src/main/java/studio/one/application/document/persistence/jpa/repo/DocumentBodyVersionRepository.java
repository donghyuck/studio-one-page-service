package studio.one.application.document.persistence.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import studio.one.application.document.persistence.jpa.entity.BodyVersionId;
import studio.one.application.document.persistence.jpa.entity.DocumentBodyVersionEntity;

public interface DocumentBodyVersionRepository extends JpaRepository<DocumentBodyVersionEntity, BodyVersionId> {
    void deleteByIdDocumentId(Long documentId);
}
