package studio.one.application.document.domain.type;

import java.util.Locale;

public enum DocumentState {
    INCOMPLETE,
    APPROVAL,
    PUBLISHED,
    REJECTED,
    ARCHIVED,
    DELETED,
    NONE;

    public static DocumentState fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NONE;
        }
        try {
            return DocumentState.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return NONE;
        }
    }
}
