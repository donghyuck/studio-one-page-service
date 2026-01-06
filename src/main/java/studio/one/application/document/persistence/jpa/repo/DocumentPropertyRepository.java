package studio.one.application.document.persistence.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import studio.one.application.document.persistence.jpa.entity.DocumentPropertyEntity;
import studio.one.application.document.persistence.jpa.entity.PropertyId;

public interface DocumentPropertyRepository extends JpaRepository<DocumentPropertyEntity, PropertyId> {
    void deleteByIdDocumentId(Long documentId);
}
