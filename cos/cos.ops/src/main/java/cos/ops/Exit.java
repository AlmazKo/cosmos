package cos.ops;

import java.nio.ByteBuffer;

public record Exit(
        @Override byte code,
        @Override int id,
        @Override int userId,
        @Override int tick
) implements OutOp {

    public Exit(int id, int userId) {
        this(Op.EXIT, id, userId, 0);
    }

    public static Exit read(ByteBuffer b) {
        return new Exit(b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}
