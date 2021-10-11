package cos.ops.out;

import cos.ops.Op;
import cos.ops.OutOp;

import java.nio.ByteBuffer;

public record ForcedExit(
        @Override byte code,
        @Override int id,
        @Override int userId,
        @Override int tick
) implements OutOp {

    public ForcedExit(int id, int userId) {
        this(Op.FORCED_EXIT, id, userId, 0);
    }

    public static ForcedExit read(ByteBuffer b) {
        return new ForcedExit(b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}
