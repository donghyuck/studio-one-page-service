package studio.one.application.document.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateBlockCommand {
    private final long blockId;
    private final Long parentBlockId;
    private final String blockType;
    private final String blockData;
    private final Integer sortOrder;
    private final long actorUserId;
    private final java.time.OffsetDateTime expectedUpdatedAt;
}
