package cos.ops.out;

import cos.ops.OutOp;

public record Death(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId,
        int victimId

) implements OutOp {


}
