package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_APPLICATION_DOCUMENT_PROPERTY")
@Data
@NoArgsConstructor
public class DocumentPropertyEntity {

    @EmbeddedId
    private PropertyId id;

    @Column(name = "PROPERTY_VALUE", nullable = false)
    private String value;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;
 
}
