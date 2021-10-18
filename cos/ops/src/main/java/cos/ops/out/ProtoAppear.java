package cos.ops.out;

import cos.ops.Direction;
import cos.ops.Op;
import cos.ops.OutOp;

import java.nio.ByteBuffer;

import static cos.ops.parser.ByteBufferUtil.*;

public record ProtoAppear(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        String world,
        int x,
        int y,
        Direction sight
) implements OutOp {

    public ProtoAppear(int id, int tick, int userId, String world, int x, int y, Direction sight) {
        this(Op.PROTO_APPEAR, id, tick, userId, world, x, y, sight);
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(x);
        buf.putInt(y);
        put(buf, sight);
        put(buf, world);
    }

    public static ProtoAppear read(ByteBuffer buf) {
        return new ProtoAppear(
                buf.getInt(),
                buf.getInt(),
                buf.getInt(),
                getString(buf),
                buf.getInt(),
                buf.getInt(),
                getEnum(buf, Direction.class)
        );
    }
}
