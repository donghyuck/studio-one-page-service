package studio.one.application.document.persistence.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import studio.one.application.document.persistence.jpa.entity.DocumentBlockVersionEntity;
import studio.one.application.document.persistence.jpa.entity.DocumentBlockVersionId;

public interface DocumentBlockVersionRepository extends JpaRepository<DocumentBlockVersionEntity, DocumentBlockVersionId> {
    List<DocumentBlockVersionEntity> findByIdDocumentIdAndIdVersionIdAndIsDeletedFalseOrderBySortOrderAscIdBlockIdAsc(
            Long documentId,
            Integer versionId);

    List<DocumentBlockVersionEntity> findByIdDocumentIdAndIdVersionIdOrderBySortOrderAscIdBlockIdAsc(
            Long documentId,
            Integer versionId);

    void deleteByIdDocumentId(Long documentId);
}
