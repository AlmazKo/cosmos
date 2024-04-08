package cos.ops.out;

import cos.ops.OutOp;

public record MeleeAttacked(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int spellId,
        int sourceId
) implements OutOp {

}
