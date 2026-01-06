package studio.one.application.document.persistence.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import studio.one.application.document.persistence.jpa.entity.DocumentBodyEntity;

public interface DocumentBodyRepository extends JpaRepository<DocumentBodyEntity, Long> {
    void deleteByDocumentId(Long documentId);
}
