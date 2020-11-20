package cos.ops;

import java.nio.ByteBuffer;

public record Move(
        @Override byte code,
        @Override int id,
        @Override int userId,
        int x,
        int y,
        Direction dir,
        Direction sight
) implements AnyOp {

    public Move(int id, int userId, int x, int y, Direction dir, Direction sight) {
        this(Op.MOVE, id, userId, x, y, dir, sight);
    }

    public static Move read(ByteBuffer b) {
        return new Move(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.of(b.get()),
                Direction.of(b.get()));
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
        buf.putInt(x);
        buf.putInt(y);
        buf.put(dir == null ? -1 : (byte) dir.ordinal());
        buf.put((byte) sight.ordinal());
    }
}
