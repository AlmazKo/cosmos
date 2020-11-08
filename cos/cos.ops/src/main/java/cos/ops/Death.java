package cos.ops;

import java.nio.ByteBuffer;

public record Death(

        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int victimId

) implements OutOp {

    public Death(int id, int tick, int userId, int victimId) {
        this(Op.DEATH, id, tick, userId, victimId);
    }

    public static Death read(ByteBuffer b) {
        return new Death(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt()
        );
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(victimId);
    }
}
