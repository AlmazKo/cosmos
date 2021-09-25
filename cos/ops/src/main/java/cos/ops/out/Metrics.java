package cos.ops.out;

import cos.ops.Op;
import cos.ops.OutOp;

import java.nio.ByteBuffer;

public record Metrics(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId,
        int lvl,
        int exp,
        int life,
        int maxLife
) implements OutOp {

    public Metrics(int id, int tick, int userId, int creatureId, int lvl, int exp, int life, int maxLife) {
        this(Op.METRICS, id, tick, userId, creatureId, lvl, exp, life, maxLife);
    }

    public static Metrics read(ByteBuffer b) {
        return new Metrics(
                b.getInt(),
                b.getInt(),
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
        buf.putInt(creatureId);
        buf.putInt(lvl);
        buf.putInt(exp);
        buf.putInt(life);
        buf.putInt(maxLife);
    }
}
