package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_APPLICATION_DOCUMENT_BODY")
@Data
@NoArgsConstructor
public class DocumentBodyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BODY_ID")
    private Long bodyId;

    @Column(name = "DOCUMENT_ID", nullable = false)
    private Long documentId;

    @Column(name = "BODY_TYPE", nullable = false)
    private Integer bodyType;

    @Lob
    @Column(name = "BODY_TEXT")
    private String bodyText;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

}
