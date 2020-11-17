package cos.olympus.game.events;

import cos.olympus.game.Creature;

public record Death(
        int id,
        int tick,
        Spell spell,
        Creature victim
) {

    public cos.ops.Death toUserOp(int userId) {
        return new cos.ops.Death(id, tick, userId, spell.source().id(), victim.id());
    }

    @Override public String toString() {
        return "Death{" +
                "id=" + id +
                ", tick=" + tick +
                ", victim=" + victim.id() +
                ", spell=" + spell.id() +
                '}';
    }
}

