package cos.olympus.ops;

import cos.olympus.game.Direction;

public final class Move implements AnyOp {
    public final int       id;
    public final int       userId;
    public final int       x;
    public final int       y;
    public final Direction dir;
    public final Direction sight;

    public Move(int id, int userId, int x, int y, Direction dir, Direction sight) {
        this.id = id;
        this.userId = userId;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.sight = sight;
    }
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
