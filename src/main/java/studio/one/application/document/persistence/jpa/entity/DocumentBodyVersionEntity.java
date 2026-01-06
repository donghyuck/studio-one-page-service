package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_APPLICATION_DOCUMENT_BODY_VERSION")
@Data
@NoArgsConstructor
public class DocumentBodyVersionEntity {

    @EmbeddedId
    private BodyVersionId id;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt; 
}
