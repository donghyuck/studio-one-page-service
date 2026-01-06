package studio.one.application.document.domain.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import studio.one.application.document.domain.vo.DocumentObjectRef;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private long documentId;
    private int objectType;
    private long objectId;
    private Long parentDocumentId;
    private Integer sortOrder;
    private String name;
    private int currentVersionId;
    private long readCount;
    private String pattern;
    private long createdBy;
    private OffsetDateTime createdAt;
    private Long updatedBy;
    private OffsetDateTime updatedAt;

    public DocumentObjectRef getObjectRef() {
        return new DocumentObjectRef(objectType, objectId);
    }
}
