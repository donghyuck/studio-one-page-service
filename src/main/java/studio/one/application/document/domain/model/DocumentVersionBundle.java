package studio.one.application.document.domain.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import studio.one.application.document.domain.type.DocumentBodyType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersionBundle {
    private Document document;
    private DocumentVersion version;
    private Integer bodyType;
    private String bodyText;
    private Map<String, String> properties;

    public DocumentBodyType getBodyTypeEnum() {
        return bodyType == null ? null : DocumentBodyType.fromId(bodyType);
    }

    public void setBodyTypeEnum(DocumentBodyType bodyType) {
        this.bodyType = bodyType == null ? null : bodyType.getId();
    }
}
