package cos.ops;

import java.nio.ByteBuffer;

public record ShotMoved(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int spellId,
        int x,
        int y,
        int speed,
        Direction dir,
        boolean finished
) implements OutOp {

    public ShotMoved(int id, int tick, int userId, int spellId, int x, int y, int speed, Direction dir, boolean finished) {
        this(Op.SHOT_MOVED, id, tick, userId, spellId, x, y, speed, dir, finished);
    }

    public static ShotMoved read(ByteBuffer b) {
        return new ShotMoved(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.of(b.get()),
                b.get() == 1
        );
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(spellId);
        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(speed);
        buf.put((byte) dir.ordinal());
        buf.put((byte) (finished ? 1 : 0));
    }
}
