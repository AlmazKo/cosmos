package cos.ops;

import java.nio.ByteBuffer;

public record Damage(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int victimId,
        int amount,
        int spellId,
        boolean crit

) implements OutOp {

    public Damage(int id, int tick, int userId, int victimId, int amount, int spellId, boolean crit) {
        this(Op.DAMAGE, id, tick, userId, victimId, amount, spellId, crit);
    }

    public static Damage read(ByteBuffer b) {
        return new Damage(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.get() == 1
        );
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(victimId);
        buf.putInt(amount);
        buf.putInt(spellId);
        buf.put((byte) (crit ? 1 : 0));
    }
}
