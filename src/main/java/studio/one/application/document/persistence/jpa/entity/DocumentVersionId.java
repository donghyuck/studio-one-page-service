package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DocumentVersionId implements Serializable {
    @Column(name = "DOCUMENT_ID")
    private Long documentId;

    @Column(name = "VERSION_ID")
    private Integer versionId;

    public DocumentVersionId() {}

    public DocumentVersionId(Long documentId, Integer versionId) {
        this.documentId = documentId;
        this.versionId = versionId;
    }

    public Long getDocumentId() { return documentId; }
    public Integer getVersionId() { return versionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentVersionId)) return false;
        DocumentVersionId that = (DocumentVersionId) o;
        return Objects.equals(documentId, that.documentId) && Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, versionId);
    }
}
