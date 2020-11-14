package cos.ops;

import java.nio.ByteBuffer;

public record MeleeAttack(
        @Override byte code,
        @Override int id,
        @Override int userId
) implements AnyOp {

    public MeleeAttack(int id, int userId) {
        this(Op.MELEE_ATTACK, id, userId);
    }

    public static MeleeAttack read(ByteBuffer b) {
        return new MeleeAttack(
                b.getInt(),
                b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(userId);
    }
}
