package cos.ops.out;

import cos.ops.OutOp;

public record Metrics(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId,
        int lvl,
        int exp,
        int life,
        int maxLife
) implements OutOp {


}
