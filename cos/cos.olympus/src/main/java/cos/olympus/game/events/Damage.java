package cos.olympus.game.events;

import cos.olympus.game.Creature;

public record Damage(
        int id,
        int tick,
        Creature victim,
        Spell spell,
        int amount
) {

    public cos.ops.Damage toOp(int userId) {
        return new cos.ops.Damage(id, tick, userId, victim.id(), spell.id(), amount);
    }
}

