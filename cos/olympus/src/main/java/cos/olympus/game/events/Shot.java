package cos.olympus.game.events;

import cos.olympus.game.Creature;
import cos.ops.Direction;

public record Shot(
        @Override int id,
        int x,
        int y,
        int speed,
        Direction dir,
        int distance,
        int tick,
        @Override Creature source

) implements Spell, Event {
//
//    public static final boolean finished = ;


}
