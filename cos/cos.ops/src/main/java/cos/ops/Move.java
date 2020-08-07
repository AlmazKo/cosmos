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
                Direction.values()[b.get()],
                Direction.values()[b.get()]);
    }

    public void write(ByteBuffer buf) {
        throw new RuntimeException("Not impemented");
    }
}
