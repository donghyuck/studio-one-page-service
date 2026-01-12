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

    @Query(
        value = "SELECT d.\"DOCUMENT_ID\" AS documentId, " +
                "d.\"OBJECT_TYPE\" AS objectType, " +
                "d.\"OBJECT_ID\" AS objectId, " +
                "d.\"PARENT_DOCUMENT_ID\" AS parentDocumentId, " +
                "d.\"SORT_ORDER\" AS sortOrder, " +
                "d.\"NAME\" AS name, " +
                "v.\"TITLE\" AS title, " +
                "d.\"VERSION_ID\" AS latestVersionId, " +
                "d.\"CREATED_BY\" AS createdBy, " +
                "d.\"UPDATED_BY\" AS updatedBy, " +
                "d.\"CREATED_AT\" AS createdAt, " +
                "d.\"UPDATED_AT\" AS updatedAt " +
                "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                "LEFT JOIN \"TB_APPLICATION_DOCUMENT_VERSION\" v " +
                "  ON v.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" AND v.\"VERSION_ID\" = d.\"VERSION_ID\" " +
                "ORDER BY d.\"DOCUMENT_ID\" DESC",
        countQuery = "SELECT COUNT(*) FROM \"TB_APPLICATION_DOCUMENT\"",
        nativeQuery = true)
    Page<DocumentSummaryProjection> findSummaryAll(Pageable pageable);

    @Query(
        value = "SELECT d.\"DOCUMENT_ID\" AS documentId, " +
                "d.\"OBJECT_TYPE\" AS objectType, " +
                "d.\"OBJECT_ID\" AS objectId, " +
                "d.\"PARENT_DOCUMENT_ID\" AS parentDocumentId, " +
                "d.\"SORT_ORDER\" AS sortOrder, " +
                "d.\"NAME\" AS name, " +
                "v.\"TITLE\" AS title, " +
                "d.\"VERSION_ID\" AS latestVersionId, " +
                "d.\"CREATED_BY\" AS createdBy, " +
                "d.\"UPDATED_BY\" AS updatedBy, " +
                "d.\"CREATED_AT\" AS createdAt, " +
                "d.\"UPDATED_AT\" AS updatedAt " +
                "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                "LEFT JOIN \"TB_APPLICATION_DOCUMENT_VERSION\" v " +
                "  ON v.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" AND v.\"VERSION_ID\" = d.\"VERSION_ID\" " +
                "WHERE d.\"OBJECT_TYPE\" = :objectType AND d.\"OBJECT_ID\" = :objectId " +
                "ORDER BY d.\"DOCUMENT_ID\" DESC",
        countQuery = "SELECT COUNT(*) FROM \"TB_APPLICATION_DOCUMENT\" " +
                     "WHERE \"OBJECT_TYPE\" = :objectType AND \"OBJECT_ID\" = :objectId",
        nativeQuery = true)
    Page<DocumentSummaryProjection> findSummaryByObjectTypeAndObjectId(@Param("objectType") int objectType,
            @Param("objectId") long objectId,
            Pageable pageable);

    @Query(
        value = "SELECT d.\"DOCUMENT_ID\" AS documentId, " +
                "d.\"OBJECT_TYPE\" AS objectType, " +
                "d.\"OBJECT_ID\" AS objectId, " +
                "d.\"PARENT_DOCUMENT_ID\" AS parentDocumentId, " +
                "d.\"SORT_ORDER\" AS sortOrder, " +
                "d.\"NAME\" AS name, " +
                "v.\"TITLE\" AS title, " +
                "d.\"VERSION_ID\" AS latestVersionId, " +
                "d.\"CREATED_BY\" AS createdBy, " +
                "d.\"UPDATED_BY\" AS updatedBy, " +
                "d.\"CREATED_AT\" AS createdAt, " +
                "d.\"UPDATED_AT\" AS updatedAt " +
                "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                "LEFT JOIN \"TB_APPLICATION_DOCUMENT_VERSION\" v " +
                "  ON v.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" AND v.\"VERSION_ID\" = d.\"VERSION_ID\" " +
                "WHERE d.\"PARENT_DOCUMENT_ID\" = :parentDocumentId " +
                "ORDER BY d.\"SORT_ORDER\" ASC, d.\"DOCUMENT_ID\" ASC",
        countQuery = "SELECT COUNT(*) FROM \"TB_APPLICATION_DOCUMENT\" " +
                     "WHERE \"PARENT_DOCUMENT_ID\" = :parentDocumentId",
        nativeQuery = true)
    Page<DocumentSummaryProjection> findSummaryByParentDocumentId(@Param("parentDocumentId") Long parentDocumentId,
            Pageable pageable);

    @Query(
        value = "SELECT d.\"DOCUMENT_ID\" AS documentId, " +
                "d.\"OBJECT_TYPE\" AS objectType, " +
                "d.\"OBJECT_ID\" AS objectId, " +
                "d.\"PARENT_DOCUMENT_ID\" AS parentDocumentId, " +
                "d.\"SORT_ORDER\" AS sortOrder, " +
                "d.\"NAME\" AS name, " +
                "v.\"TITLE\" AS title, " +
                "d.\"VERSION_ID\" AS latestVersionId, " +
                "d.\"CREATED_BY\" AS createdBy, " +
                "d.\"UPDATED_BY\" AS updatedBy, " +
                "d.\"CREATED_AT\" AS createdAt, " +
                "d.\"UPDATED_AT\" AS updatedAt " +
                "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                "LEFT JOIN \"TB_APPLICATION_DOCUMENT_VERSION\" v " +
                "  ON v.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" AND v.\"VERSION_ID\" = d.\"VERSION_ID\" " +
                "WHERE d.\"PARENT_DOCUMENT_ID\" IS NULL " +
                "ORDER BY d.\"SORT_ORDER\" ASC, d.\"DOCUMENT_ID\" ASC",
        countQuery = "SELECT COUNT(*) FROM \"TB_APPLICATION_DOCUMENT\" " +
                     "WHERE \"PARENT_DOCUMENT_ID\" IS NULL",
        nativeQuery = true)
    Page<DocumentSummaryProjection> findSummaryByParentDocumentIdIsNull(Pageable pageable);

    @Query(
        value = "SELECT DISTINCT d.\"DOCUMENT_ID\" AS documentId, " +
                "d.\"OBJECT_TYPE\" AS objectType, " +
                "d.\"OBJECT_ID\" AS objectId, " +
                "d.\"PARENT_DOCUMENT_ID\" AS parentDocumentId, " +
                "d.\"SORT_ORDER\" AS sortOrder, " +
                "d.\"NAME\" AS name, " +
                "v.\"TITLE\" AS title, " +
                "d.\"VERSION_ID\" AS latestVersionId, " +
                "d.\"CREATED_BY\" AS createdBy, " +
                "d.\"UPDATED_BY\" AS updatedBy, " +
                "d.\"CREATED_AT\" AS createdAt, " +
                "d.\"UPDATED_AT\" AS updatedAt " +
                "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                "LEFT JOIN \"TB_APPLICATION_DOCUMENT_BODY\" b ON b.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" " +
                "LEFT JOIN \"TB_APPLICATION_DOCUMENT_VERSION\" v " +
                "  ON v.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" AND v.\"VERSION_ID\" = d.\"VERSION_ID\" " +
                "WHERE LOWER(d.\"NAME\") LIKE :keyword OR LOWER(b.\"BODY_TEXT\") LIKE :keyword " +
                "ORDER BY d.\"DOCUMENT_ID\" DESC",
        countQuery = "SELECT COUNT(DISTINCT d.\"DOCUMENT_ID\") " +
                     "FROM \"TB_APPLICATION_DOCUMENT\" d " +
                     "LEFT JOIN \"TB_APPLICATION_DOCUMENT_BODY\" b ON b.\"DOCUMENT_ID\" = d.\"DOCUMENT_ID\" " +
                     "WHERE LOWER(d.\"NAME\") LIKE :keyword OR LOWER(b.\"BODY_TEXT\") LIKE :keyword",
        nativeQuery = true)
    Page<DocumentSummaryProjection> findSummaryByNameOrBody(@Param("keyword") String keyword, Pageable pageable);
}
