package cos.ops.out;

import cos.ops.OutOp;

public record Disconnect(
        @Override int id,
        int userId
) implements OutOp {

}
