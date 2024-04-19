package cos.ops.out;

import cos.ops.OutOp;

public record Metrics(
        @Override int id,
        @Override int userId,
        int actorId,
        int lvl,
        int exp,
        int life,
        int maxLife
) implements OutOp {


}
