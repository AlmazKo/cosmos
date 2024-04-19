package cos.ops.out;

import cos.ops.OutOp;

public record MeleeAttack(
        @Override int id,
        @Override int userId,
        int spellId,
        int sourceId
) implements OutOp {

}
