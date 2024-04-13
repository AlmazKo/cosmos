package cos.ops.out;

import cos.ops.OutOp;

public record ActorHid(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId
) implements OutOp {

}
