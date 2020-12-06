package cos.ops.in;

import cos.ops.InOp;
import cos.ops.Op;

import java.nio.ByteBuffer;

public record ForcedExit(
        @Override byte code,
        @Override int id,
        @Override int userId,
        @Override int tick
) implements InOp {

    public ForcedExit(int id, int userId) {
        this(Op.EXIT, id, userId, 0);
    }

    public static ForcedExit read(ByteBuffer b) {
        return new ForcedExit(b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}
