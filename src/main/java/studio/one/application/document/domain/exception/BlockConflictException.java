package studio.one.application.document.domain.exception;

public class BlockConflictException extends RuntimeException {

    private final long blockId;

    public BlockConflictException(long blockId) {
        super("block update conflict: " + blockId);
        this.blockId = blockId;
    }

    public long getBlockId() {
        return blockId;
    }
}
