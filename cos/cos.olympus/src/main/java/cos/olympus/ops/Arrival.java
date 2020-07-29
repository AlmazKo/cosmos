package cos.olympus.ops;

import cos.olympus.game.Direction;

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
        buf.putInt(id);
        buf.putInt(x);
        buf.putInt(x);
        buf.put((byte) dir.ordinal());
        buf.put((byte) sight.ordinal());
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
