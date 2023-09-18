package cos.ops.out;

import cos.ops.Direction;
import cos.ops.SomeOp;

public record TeleportIn(
        int id,
        int tick,
        int userId,
        String world,
        int x,
        int y,
        Direction sight
) implements SomeOp {

}
