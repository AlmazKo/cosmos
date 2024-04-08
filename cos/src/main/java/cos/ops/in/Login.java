package cos.ops.in;

import cos.ops.InOp;

public record Login(
        @Override int id,
        @Override int userId
) implements InOp {

}
