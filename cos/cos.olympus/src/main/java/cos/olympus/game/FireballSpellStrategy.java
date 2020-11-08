package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.game.events.Fireball;
import cos.ops.OutOp;

import java.util.Collection;

public class FireballSpellStrategy implements SpellStrategy {

    private final static Logger logger = new Logger(Movements.class);

    private static int SPELL_IDS = 0;//todo move from here

    public final Fireball spell;
    private final World    world;
    private final int      id;
    public       boolean  finished;
    private       int      passed;
    public       int      x;
    public       int      y;

    public FireballSpellStrategy(Fireball spell, World world) {
        this.spell = spell;
        this.world = world;
        this.id = ++SPELL_IDS;
        this.x = spell.x();
        this.y = spell.y();
    }

    @Override public int id() {
        return id;
    }

    public boolean onTick(int tick, Collection<OutOp> consumer) {

        int distance = (tick - spell.tickId()) * spell.speed() / 100;

        x = spell.x();
        y = spell.y();
        if (spell.dir().isX()) {
            x += distance;
        } else {
            y += distance;
        }

        var victim = world.getCreature(x, y);
        if (distance >= spell.distance()) {
            finished = true;
        }

        if (distance > passed) {
//            consumer.add(new FireballMoved(id, tick, spell.source().id, 0, x, y, spell.speed(), spell.dir(), finished));
            //
            passed = distance;
        }

//        logger.info("Spell distance: " + distance);
        return finished;
    }

    @Override public boolean inZone(Creature cr) {
        return Util.inZone(cr, x, y, 8);
    }

}


/*

    val currentX: Int get() = x + (if (direction === WEST) -distanceTravelled else if (direction == EAST) distanceTravelled else 0)

    val currentY: Int get() = y + (if (direction === NORTH) -distanceTravelled else if (direction == SOUTH) distanceTravelled else 0)


      val distance = Math.min(action.distance, Math.round((time - action.time) / action.speed.toFloat()))
        action.distanceTravelled = distance

        val x = action.currentX
        val y = action.currentY

        val victim = map.getCreature(x, y)
        if (victim !== null && victim.id != action.source.id) {
            val d = Damage(x, y, time, victim, action.source, 25, action.id)
            victim.damage(d)
            actions.add(d)

            if (victim.state.life == 0) {
                actions.add(Death(d))
            }

            action.finished = true
        }

        if (distance >= action.distance) {
            action.finished = true
        }

        return action.finished
 */
