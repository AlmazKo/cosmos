package cos.ops.out;

import cos.ops.Direction;
import cos.ops.OutOp;

public record ActorMoved(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int actorId,
        int x,
        int y,
        int offset,
        int speed,
        Direction mv,
        Direction sight
) implements OutOp {
}
