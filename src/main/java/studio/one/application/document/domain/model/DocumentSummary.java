package studio.one.application.document.domain.model;

import java.time.OffsetDateTime;

public record DocumentSummary(
        long documentId,
        Integer objectType,
        Long objectId,
        Long parentDocumentId,
        Integer sortOrder,
        String name,
        String title,
        Integer latestVersionId,
        long createdBy,
        Long updatedBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
