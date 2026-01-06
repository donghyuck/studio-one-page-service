package studio.one.application.document.persistence.jpa.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentBlockVersionId implements Serializable {

    @Column(name = "BLOCK_ID")
    private Long blockId;

    @Column(name = "DOCUMENT_ID")
    private Long documentId;

    @Column(name = "VERSION_ID")
    private Integer versionId;

    public Long getBlockId() {
        return blockId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public Integer getVersionId() {
        return versionId;
    }
}
