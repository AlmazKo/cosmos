package cos.ops.in;

import cos.ops.Direction;
import cos.ops.InOp;

public record StopMove(
        @Override int id,
        @Override int userId,
        int x,
        int y,
        Direction sight
) implements InOp {


}
