package cos.ops;

import java.nio.ByteBuffer;

public record FireballEmmit(
        @Override byte code,
        @Override int id,
        @Override int userId
) implements AnyOp {

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
