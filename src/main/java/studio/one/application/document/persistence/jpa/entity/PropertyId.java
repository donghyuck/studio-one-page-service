package studio.one.application.document.persistence.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PropertyId implements Serializable {

    @Column(name = "DOCUMENT_ID")
    private Long documentId;

    @Column(name = "VERSION_ID")
    private Integer versionId;

    @Column(name = "PROPERTY_NAME")
    private String propertyName;

    public PropertyId() {}

    public PropertyId(Long documentId, Integer versionId, String propertyName) {
        this.documentId = documentId;
        this.versionId = versionId;
        this.propertyName = propertyName;
    }

    public Long getDocumentId() { return documentId; }
    public Integer getVersionId() { return versionId; }
    public String getPropertyName() { return propertyName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyId)) return false;
        PropertyId that = (PropertyId) o;
        return Objects.equals(documentId, that.documentId) &&
               Objects.equals(versionId, that.versionId) &&
               Objects.equals(propertyName, that.propertyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, versionId, propertyName);
    }
}
