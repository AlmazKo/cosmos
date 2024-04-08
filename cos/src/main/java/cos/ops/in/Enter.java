package cos.ops.in;

import cos.ops.InOp;


public record Enter(
        @Override int id,
        @Override int userId,
        @Override String world
) implements InOp {

}
