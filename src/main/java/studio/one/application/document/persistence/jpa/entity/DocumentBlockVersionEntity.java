package studio.one.application.document.persistence.jpa.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_APPLICATION_DOCUMENT_BLOCK_VERSION")
@Data
@NoArgsConstructor
public class DocumentBlockVersionEntity {

    @EmbeddedId
    private DocumentBlockVersionId id;

    @Column(name = "PARENT_BLOCK_ID")
    private Long parentBlockId;

    @Column(name = "BLOCK_TYPE", nullable = false)
    private String blockType;

    @Column(name = "BLOCK_DATA")
    private String blockData;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "IS_DELETED", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;
}
