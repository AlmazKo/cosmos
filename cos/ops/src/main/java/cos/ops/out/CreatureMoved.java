package cos.ops.out;

import cos.ops.Direction;
import cos.ops.OutOp;

public record CreatureMoved(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId,
        int x,
        int y,
        int offset,
        int speed,
        Direction mv,
        Direction sight
) implements OutOp {
}
