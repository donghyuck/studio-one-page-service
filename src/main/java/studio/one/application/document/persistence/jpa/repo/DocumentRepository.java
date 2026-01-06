package studio.one.application.document.persistence.jpa.repo;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import studio.one.application.document.persistence.jpa.entity.DocumentEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DocumentEntity d where d.documentId = :id")
    Optional<DocumentEntity> findByIdForUpdate(@Param("id") Long id);

    Page<DocumentEntity> findByObjectTypeAndObjectId(int objectType, long objectId, Pageable pageable);

    Page<DocumentEntity> findByParentDocumentId(Long parentDocumentId, Pageable pageable);

    Page<DocumentEntity> findByParentDocumentIdIsNull(Pageable pageable);

    @Query(
        value = "SELECT DISTINCT d.* " +
                "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                "LEFT JOIN \"TB_APPLICATION_DOCUMENT_BODY\" b ON b.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" " +
                "WHERE LOWER(d.\"NAME\") LIKE :keyword OR LOWER(b.\"BODY_TEXT\") LIKE :keyword",
        countQuery = "SELECT COUNT(DISTINCT d.\"DOCUMENT_ID\") " +
                     "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                     "LEFT JOIN \"TB_APPLICATION_DOCUMENT_BODY\" b ON b.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" " +
                     "WHERE LOWER(d.\"NAME\") LIKE :keyword OR LOWER(b.\"BODY_TEXT\") LIKE :keyword",
        nativeQuery = true)
    Page<DocumentEntity> findByNameOrBody(@Param("keyword") String keyword, Pageable pageable);
}
