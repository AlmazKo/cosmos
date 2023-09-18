package cos.ops.out;

import cos.ops.OutOp;

public record CreatureHid(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId
) implements OutOp {

}
