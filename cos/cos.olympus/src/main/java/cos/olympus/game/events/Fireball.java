package cos.olympus.game.events;

import cos.olympus.game.Creature;
import cos.ops.Direction;

public record Fireball(
        @Override int id,
        int x,
        int y,
        int speed,
        Direction dir,
        int distance,
        int tickId,
        @Override Creature source

) implements Spell {
//
//    public static final boolean finished = ;


}
