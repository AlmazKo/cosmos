package cos.ops;

import java.nio.ByteBuffer;

public record CreatureMoved(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId,
        int x,
        int y,
        int offset,
        int speed,
        Direction mv,
        Direction sight
) implements OutOp {

    public CreatureMoved(int id, int tick, int userId, int creatureId, int x, int y, int offset, int speed, Direction dir, Direction sight) {
        this(Op.CREATURE_MOVED, id, tick, userId, creatureId, x, offset, y, speed, dir, sight);
    }

    public static CreatureMoved read(ByteBuffer b) {
        return new CreatureMoved(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.of(b.get()),
                Direction.of(b.get()));
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(creatureId);
        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(offset);
        buf.putInt(speed);
        if (mv == null) {
            buf.put((byte) -1);
        } else {
            buf.put((byte) mv.ordinal());
        }
        buf.put((byte) sight.ordinal());
    }
}
