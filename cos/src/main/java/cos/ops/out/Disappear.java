package cos.ops.out;

import cos.ops.OutOp;

public record Disappear(
        @Override int id,
        @Override int userId,
        int actorId
) implements OutOp {

}
