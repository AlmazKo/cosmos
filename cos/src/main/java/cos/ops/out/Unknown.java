package cos.ops.out;

import cos.ops.OutOp;

public record Unknown(
        @Override int id,
        @Override int userId
) implements OutOp {

}
