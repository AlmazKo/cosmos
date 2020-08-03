package cos.ops;

import java.nio.ByteBuffer;

public record Arrival(
        @Override int id,
        int userId,
        int x,
        int y,
        Direction dir,
        Direction sight
) implements OutOp {


    public void write(ByteBuffer buf) {
        buf.put(Op.APPEAR);
        buf.putInt(id);
        buf.putInt(userId);
        buf.putInt(x);
        buf.putInt(y);
        buf.put((byte) dir.ordinal());
        buf.put((byte) sight.ordinal());
    }

    public static Arrival create(ByteBuffer b) {
        return new Arrival(
                b.getInt(),
                b.getInt(),
                b.getInt(),
                b.getInt(),
                Direction.values()[b.get()],
                Direction.values()[b.get()]);
    }

    @Override public String toString() {
        return "{" +
                "id:" + id +
                ", userId:" + userId +
                ", x=" + x +
                ", y=" + y +
                ", dir=" + dir +
                ", sight=" + sight +
                '}';
    }


//    public Arrival(int id, int userId, int x, int y, Direction dir, Direction sight) {
//        this.id = id;
//        this.userId = userId;
//        this.x = x;
//        this.y = y;
//        this.dir = dir;
//        this.sight = sight;
//    }
//
//    public Arrival(ByteBuffer b) {
//        id = b.getInt();
//        userId = b.getInt();
//        x = b.getInt();
//        y = b.getInt();
//        dir = Direction.values()[b.get()];
//        sight = Direction.values()[b.get()];
//    }
}

 /*: AnyOp {

//    Move(ByteBuffer b) {
//        id = b.int,
//        userId = b.int,
//        x = b.int,
//        y = b.int,
//        dir = b.get().toEnum(),
//        sight = b.get().toEnum()
    }


}?

//internal fun Byte.toEnum(): Direction {
//    return Direction.values()[this.toInt()]
//}
*/
