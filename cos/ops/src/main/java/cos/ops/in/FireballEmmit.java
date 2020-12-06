package cos.ops.in;

import cos.ops.InOp;
import cos.ops.Op;

import java.nio.ByteBuffer;

public record FireballEmmit(
        @Override byte code,
        @Override int id,
        @Override int userId
) implements InOp {

    public FireballEmmit(int id, int userId) {
        this(Op.EMMIT_FIREBALL, id, userId);
    }

    public static FireballEmmit read(ByteBuffer b) {
        return new FireballEmmit(
                b.getInt(),
                b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}
