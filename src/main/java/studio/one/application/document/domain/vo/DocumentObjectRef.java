package studio.one.application.document.domain.vo;

public class DocumentObjectRef {
    private final int objectType;
    private final long objectId;

    public DocumentObjectRef(int objectType, long objectId) {
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public int getObjectType() {
        return objectType;
    }

    public long getObjectId() {
        return objectId;
    }
}
