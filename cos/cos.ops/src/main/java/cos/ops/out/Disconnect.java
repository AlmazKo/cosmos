package cos.ops.out;

import cos.ops.Op;
import cos.ops.OutOp;

import java.nio.ByteBuffer;

public record Disconnect(
        @Override byte code,
        @Override int id,
        @Override int tick,
        int userId
) implements OutOp {

    public Disconnect(int id, int tick, int userId) {
        this(Op.DISCONNECT, id, tick, userId);
    }

    public static Disconnect read(ByteBuffer b) {
        return new Disconnect(b.getInt(), b.getInt(), b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}
