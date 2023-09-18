package cos.ops.out;

import cos.ops.OutOp;

public record ForcedExit(
        @Override int id,
        @Override int userId,
        @Override int tick
) implements OutOp {

}
