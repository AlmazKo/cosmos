package cos.ops.in;

import cos.ops.Direction;
import cos.ops.InOp;

public record Move(
        @Override int id,
        @Override int userId,
        int x,
        int y,
        Direction dir,
        Direction sight
) implements InOp {

}
