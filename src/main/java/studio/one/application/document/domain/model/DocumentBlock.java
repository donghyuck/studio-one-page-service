package studio.one.application.document.domain.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentBlock {
    private long blockId;
    private long documentId;
    private Long parentBlockId;
    private String blockType;
    private String blockData;
    private Integer sortOrder;
    private boolean deleted;
    private long createdBy;
    private OffsetDateTime createdAt;
    private Long updatedBy;
    private OffsetDateTime updatedAt;
}
