package cos.olympus.game.events;

import cos.olympus.game.Creature;

public record Damage(
        int id,
        int tick,
        Creature victim,
        Spell spell,
        int amount,
        boolean crit
) implements Event {

    public cos.ops.out.Damage toUserOp(int userId) {
        return new cos.ops.out.Damage(id, tick, userId, spell.source().id(), victim.id(), amount, spell.id(), crit);
    }

    @Override public String toString() {
        return "Damage{" +
                "id=" + id +
                ", tick=" + tick +
                ", victim=" + victim.id() +
                ", spell=" + spell.id() +
                ", amount=" + amount +
                '}';
    }
}

