package studio.one.application.document.command;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class CreateVersionCommand {
    private final String title;
    private final String bodyText;
    private final int bodyType;
    private final Map<String, String> properties;
    private final long actorUserId;
}
