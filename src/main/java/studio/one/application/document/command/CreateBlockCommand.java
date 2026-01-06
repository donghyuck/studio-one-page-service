package studio.one.application.document.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateBlockCommand {
    private final long documentId;
    private final Long parentBlockId;
    private final String blockType;
    private final String blockData;
    private final Integer sortOrder;
    private final long actorUserId;
}
