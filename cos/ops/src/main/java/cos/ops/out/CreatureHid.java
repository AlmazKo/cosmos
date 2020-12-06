package cos.ops.out;

import cos.ops.Op;
import cos.ops.OutOp;

import java.nio.ByteBuffer;

public record CreatureHid(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId
) implements OutOp {

    public CreatureHid(int id, int tick, int userId, int creatureId) {
        this(Op.CREATURE_HID, id, tick, userId, creatureId);
    }

    public static CreatureHid read(ByteBuffer b) {
        return new CreatureHid(
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
    }
}
