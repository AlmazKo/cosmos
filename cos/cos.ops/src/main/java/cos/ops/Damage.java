package cos.ops;

import java.nio.ByteBuffer;

public record Damage(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int victimId,
        int amount,
        int spellId

) implements OutOp {

    public Damage(int id, int tick, int userId, int victimId, int amount, int spellId) {
        this(Op.DAMAGE, id, tick, userId, victimId, amount, spellId);
    }

    public static Damage read(ByteBuffer b) {
        return new Damage(
                b.getInt(),
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
        buf.putInt(victimId);
        buf.putInt(amount);
        buf.putInt(spellId);
    }
}
