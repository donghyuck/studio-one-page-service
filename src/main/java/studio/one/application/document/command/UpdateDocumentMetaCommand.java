package studio.one.application.document.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateDocumentMetaCommand {
    private final long documentId;
    private final String name;
    private final String pattern;
    private final long actorUserId;
    private final java.time.OffsetDateTime expectedUpdatedAt;
}
