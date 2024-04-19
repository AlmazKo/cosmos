package cos.ops.out;

import cos.ops.OutOp;

public record Damage(
        @Override int id,
        @Override int userId,
        int sourceId,
        int victimId,
        int amount,
        int spellId,
        boolean crit

) implements OutOp {

}
