package cos.ops.out;

import cos.ops.Direction;
import cos.ops.OutOp;

public record ProtoAppear(
        @Override int id,
        @Override int userId,
        String world,
        int x,
        int y,
        Direction sight
) implements OutOp {

}
