package studio.one.application.document.persistence.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import studio.one.application.document.persistence.jpa.entity.DocumentBlockEntity;

public interface DocumentBlockRepository extends JpaRepository<DocumentBlockEntity, Long> {
    List<DocumentBlockEntity> findByDocumentIdOrderBySortOrderAscBlockIdAsc(Long documentId);

    void deleteByDocumentId(Long documentId);
}
