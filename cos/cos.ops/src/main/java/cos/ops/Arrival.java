package cos.ops;

import java.nio.ByteBuffer;

public record Arrival(
        @Override byte code,
        @Override int id,
        @Override int tick,
        int userId,
        int x,
        int y,
        Direction dir,
        Direction sight
) implements OutOp {

    public Arrival(int id, int tick, int userId, int x, int y, Direction dir, Direction sight) {
        this(Op.APPEAR, id, tick, userId, x, y, dir, sight);
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(x);
        buf.putInt(y);
        buf.put((byte) dir.ordinal());
        buf.put((byte) sight.ordinal());
    }

    public static Arrival read(ByteBuffer b) {
        return new Arrival(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.values()[b.get()],
                Direction.values()[b.get()]);
    }
//
//    @Override public String toString() {
//        return "{" +
//                "id:" + id +
//                ", userId:" + userId +
//                ", x=" + x +
//                ", y=" + y +
//                ", dir=" + dir +
//                ", sight=" + sight +
//                '}';
}
