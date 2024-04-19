package cos.ops.out;

import cos.ops.Direction;
import cos.ops.OutOp;

public record Move(
        @Override int id,
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
