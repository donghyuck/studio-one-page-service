package studio.one.application.document.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteBlockCommand {
    private final long blockId;
    private final long actorUserId;
    private final java.time.OffsetDateTime expectedUpdatedAt;
}
