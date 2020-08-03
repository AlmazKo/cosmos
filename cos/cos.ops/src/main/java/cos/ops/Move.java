package cos.ops;

import java.nio.ByteBuffer;

public record Move(
        int id,
        int userId,
        int x,
        int y,
        Direction dir,
        Direction sight
) implements AnyOp {

    public static Move create(ByteBuffer b) {
        return new Move(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.values()[b.get()],
                Direction.values()[b.get()]);
    }
}
