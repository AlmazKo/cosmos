package cos.ops.in;

import cos.ops.InOp;

public record ShotEmmit(
        @Override int id,
        @Override int userId
) implements InOp {

}
