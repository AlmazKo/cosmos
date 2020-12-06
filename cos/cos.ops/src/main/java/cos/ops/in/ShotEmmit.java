package cos.ops.in;

import cos.ops.InOp;
import cos.ops.Op;

import java.nio.ByteBuffer;

public record ShotEmmit(
        @Override byte code,
        @Override int id,
        @Override int userId
) implements InOp {

    public ShotEmmit(int id, int userId) {
        this(Op.EMMIT_SHOT, id, userId);
    }

    public static ShotEmmit read(ByteBuffer b) {
        return new ShotEmmit(
                b.getInt(),
                b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}
