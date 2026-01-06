package studio.one.application.document.command;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateDocumentCommand {
    private final int objectType;
    private final long objectId;
    private final Long parentDocumentId;
    private final Integer sortOrder;
    private final String name;
    private final String title;
    private final String bodyText;
    private final int bodyType;
    private final Map<String, String> properties;
    private final long actorUserId;

}
