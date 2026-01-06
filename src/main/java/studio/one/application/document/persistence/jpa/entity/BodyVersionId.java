package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BodyVersionId implements Serializable {
    @Column(name = "BODY_ID")
    private Long bodyId;

    @Column(name = "DOCUMENT_ID")
    private Long documentId;

    @Column(name = "VERSION_ID")
    private Integer versionId;

    public BodyVersionId() {}

    public BodyVersionId(Long bodyId, Long documentId, Integer versionId) {
        this.bodyId = bodyId;
        this.documentId = documentId;
        this.versionId = versionId;
    }

    public Long getBodyId() { return bodyId; }
    public Long getDocumentId() { return documentId; }
    public Integer getVersionId() { return versionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BodyVersionId)) return false;
        BodyVersionId that = (BodyVersionId) o;
        return Objects.equals(bodyId, that.bodyId) &&
               Objects.equals(documentId, that.documentId) &&
               Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bodyId, documentId, versionId);
    }
}
