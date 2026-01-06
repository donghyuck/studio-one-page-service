package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_APPLICATION_DOCUMENT_VERSION")
@Data
@NoArgsConstructor
public class DocumentVersionEntity {

    @EmbeddedId
    private DocumentVersionId id;

    @Column(name = "STATE")
    private String state;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SECURED")
    private Boolean secured = true;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "TEMPLATE")
    private String template;

    @Column(name = "SCRIPT")
    private String script;

    @Column(name = "PATTERN")
    private String pattern;

    @Column(name = "SUMMARY")
    private String summary;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;
 
}
