package cos.ops.out;

import cos.ops.Op;
import cos.ops.OutOp;

import java.nio.ByteBuffer;

public record MeleeAttacked(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int spellId,
        int sourceId
) implements OutOp {

    public MeleeAttacked(int id, int tick, int userId, int spellId, int creatureId) {
        this(Op.MELEE_ATTACKED, id, tick, userId, spellId, creatureId);
    }

    public static MeleeAttacked read(ByteBuffer b) {
        return new MeleeAttacked(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt());
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(spellId);
        buf.putInt(sourceId);
    }
}
