package studio.one.application.document.domain.type;

public enum DocumentBodyType {
    RAW(1),
    HTML(2),
    FREEMARKER(3),
    VELOCITY(4);

    private final int id;

    DocumentBodyType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DocumentBodyType fromId(int id) {
        for (DocumentBodyType type : DocumentBodyType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        return RAW;
    }
}
