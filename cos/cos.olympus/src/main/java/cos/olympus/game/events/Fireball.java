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
        Creature source

) implements Spell {
//
//    public static final boolean finished = ;

    public static Fireball of(Creature cr, int tickId) {
        return new Fireball(0, cr.x(), cr.y(), 40, cr.sight(), 10, tickId, cr);
    }
}
/*

    override val x: Int,
    override val y: Int,
    override val time: Tsm,
    val direction: Direction,
    val Creature: Int,
    val speed: Duration,
    val source: Creature,
    var startTime: Tsm = 0,
    var distanceTravelled: Int = 0,
    override var finished: Boolean = false
 */
