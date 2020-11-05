package cos.ops;

import java.nio.ByteBuffer;

public record StopMove(
        @Override byte code,
        @Override int id,
        @Override int userId,
        int x,
        int y,
        Direction sight
) implements AnyOp {

    public StopMove(int id, int userId, int x, int y, Direction sight) {
        this(Op.STOP_MOVE, id, userId, x, y, sight);
    }

    public static StopMove read(ByteBuffer b) {
        return new StopMove(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.of(b.get()));
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
        buf.putInt(x);
        buf.putInt(y);
        buf.put((byte) sight.ordinal());
    }
}
