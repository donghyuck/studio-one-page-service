package studio.one.application.document.persistence.jpa.repo;

import java.time.OffsetDateTime;

public interface DocumentSummaryProjection {
    Long getDocumentId();
    Integer getObjectType();
    Long getObjectId();
    Long getParentDocumentId();
    Integer getSortOrder();
    String getName();
    String getTitle();
    Integer getLatestVersionId();
    Long getCreatedBy();
    Long getUpdatedBy();
    OffsetDateTime getCreatedAt();
    OffsetDateTime getUpdatedAt();
}
