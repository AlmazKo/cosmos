package cos.ops;

import java.nio.ByteBuffer;

public record ObjAppear(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int x,
        int y,
        int tileId
) implements OutOp {


    public ObjAppear(int id, int tick, int userId, int x, int y, int tileId) {
        this(Op.APPEAR_OBJ, id, tick, userId, x, y, tileId);
    }


    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(tileId);
    }


    public static ObjAppear read(ByteBuffer b) {
        return new ObjAppear(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt()
        );
    }
}
