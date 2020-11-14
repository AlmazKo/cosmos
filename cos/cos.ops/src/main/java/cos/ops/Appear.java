package cos.ops;

import java.nio.ByteBuffer;

public record Appear(
        @Override byte code,
        @Override int id,
        @Override int tick,
        @Override int userId,
        int x,
        int y,
        Direction mv,
        Direction sight,
        int life
) implements OutOp {

    public Appear(int id, int tick, int userId, int x, int y, Direction mv, Direction sight, int life) {
        this(Op.APPEAR, id, tick, userId, x, y, mv, sight, life);
    }

    public void write(ByteBuffer buf) {
        buf.putInt(id);
        buf.putInt(tick);
        buf.putInt(userId);
        buf.putInt(x);
        buf.putInt(y);
        if (mv == null) {
            buf.put((byte) -1);
        } else {
            buf.put((byte) mv.ordinal());
        }

        buf.put((byte) sight.ordinal());
        buf.putInt(life);
    }

    public static Appear read(ByteBuffer b) {

        return new Appear(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.of(b.get()),
                Direction.of(b.get()),
                b.getInt()
        );
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
