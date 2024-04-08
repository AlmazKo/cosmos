package cos.ops.out;

import cos.ops.OutOp;

public record ObjAppear(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int x,
        int y,
        int tileId
) implements OutOp {

}
