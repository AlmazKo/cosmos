package cos.olympus.game.events;

import cos.olympus.game.Creature;
import cos.olympus.game.World;
import cos.ops.Direction;

public record MeleeAttack(
        @Override int id,
        int tickId,
        int x,
        int y,
        Direction dir,
        @Override Creature source
) implements Spell {

}
