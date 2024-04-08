package cos.ops.out;

import cos.ops.Direction;
import cos.ops.OutOp;

public record FireballMoved(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int spellId,
        int x,
        int y,
        int speed,
        Direction dir,
        boolean finished
) implements OutOp {


}
