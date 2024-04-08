package cos.ops.out;

import cos.ops.Direction;
import cos.ops.OutOp;

public record Appear(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int x,
        int y,
        Direction mv,
        Direction sight,
        int lvl,
        int life
) implements OutOp {


}
