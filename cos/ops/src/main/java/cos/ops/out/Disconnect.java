package cos.ops.out;

import cos.ops.OutOp;

public record Disconnect(
        @Override int id,
        @Override int tick,
        int userId
) implements OutOp {

}
