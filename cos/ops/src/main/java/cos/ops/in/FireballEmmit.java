package cos.ops.in;

import cos.ops.InOp;

public record FireballEmmit(
        @Override int id,
        @Override int userId
) implements InOp {

}
