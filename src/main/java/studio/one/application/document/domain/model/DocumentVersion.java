package studio.one.application.document.domain.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import studio.one.application.document.domain.type.DocumentState;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVersion {
    private long documentId;
    private int versionId;
    private String state;
    private String title;
    private boolean secured;
    private String contentType;
    private String template;
    private String script;
    private String pattern;
    private String summary;
    private long createdBy;
    private OffsetDateTime createdAt;
    private Long updatedBy;
    private OffsetDateTime updatedAt;

    public DocumentState getStateEnum() {
        return DocumentState.fromString(state);
    }

    public void setStateEnum(DocumentState state) {
        this.state = state == null ? null : state.name().toLowerCase();
    }
}
