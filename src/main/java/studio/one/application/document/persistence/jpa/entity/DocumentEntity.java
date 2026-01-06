package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_APPLICATION_DOCUMENT")
@Data
@NoArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCUMENT_ID")
    private Long documentId;

    @Column(name = "OBJECT_TYPE", nullable = false)
    private Integer objectType;

    @Column(name = "OBJECT_ID", nullable = false)
    private Long objectId;

    @Column(name = "PARENT_DOCUMENT_ID")
    private Long parentDocumentId;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "VERSION_ID", nullable = false)
    private Integer currentVersionId;

    @Column(name = "READ_COUNT", nullable = false)
    private Long readCount = 0L;

    @Column(name = "PATTERN")
    private String pattern;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;
 
}
