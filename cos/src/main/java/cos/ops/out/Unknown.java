package cos.ops.out;

import cos.ops.OutOp;

public record Unknown(
        @Override int id,
        @Override int tick,
        @Override int userId
) implements OutOp {

}
