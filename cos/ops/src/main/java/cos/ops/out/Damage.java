package cos.ops.out;

import cos.ops.OutOp;

public record Damage(
        @Override int id,
        @Override int tick,
        @Override int userId,
        int creatureId,
        int victimId,
        int amount,
        int spellId,
        boolean crit

) implements OutOp {

}
