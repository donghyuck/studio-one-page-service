package studio.one.application.document.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteDocumentCommand {
    private final long documentId;
    private final java.time.OffsetDateTime expectedUpdatedAt;
}
