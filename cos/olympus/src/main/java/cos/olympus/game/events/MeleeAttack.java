package cos.olympus.game.events;

import cos.olympus.game.Creature;
import cos.ops.Direction;

public record MeleeAttack(
        @Override int id,
        int tick,
        int x,
        int y,
        Direction dir,
        @Override Creature source
) implements Spell, Event {

}
