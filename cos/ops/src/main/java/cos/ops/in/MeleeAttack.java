package cos.ops.in;

import cos.ops.InOp;

public record MeleeAttack(
        @Override int id,
        @Override int userId
) implements InOp {

}
