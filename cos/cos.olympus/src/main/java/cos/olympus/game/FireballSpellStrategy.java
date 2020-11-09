package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Fireball;
import cos.ops.OutOp;

import java.util.Collection;

public class FireballSpellStrategy implements SpellStrategy {


    private final static Logger logger = new Logger(FireballSpellStrategy.class);

    private static int DAMAGES_IDS = 0;//todo move from here
    private static int SPELL_IDS = 0;//todo move from here

    public final  Fireball spell;
    private final World    world;
    public        boolean  finished;
    private       int      passed;
    public        int      x;
    public        int      y;

    public FireballSpellStrategy(int tick, Creature cr, World world) {
        this.spell = new Fireball(++SPELL_IDS, cr.x(), cr.y(), 40, cr.sight(), 10, tick, cr);
        this.world = world;
        this.x = spell.x();
        this.y = spell.y();
    }

    @Override public int id() {
        return spell.id();
    }

    public boolean onTick(int tick, Collection<OutOp> consumer, Collection<Damage> damages) {

        int distance = (tick - spell.tickId()) * spell.speed() / 100;

        x = spell.x();
        y = spell.y();
        if (spell.dir().isX()) {
            x += distance;
        } else {
            y += distance;
        }

        var victim = world.getCreature(x, y);
        if (victim != null && spell.source().id != victim.id) {
            boolean crit = Util.rand(0, 10) == 1;
            var d = new Damage(++DAMAGES_IDS, tick, victim, spell, crit ? 100 : 30, crit);
            logger.info("Damaged : " + d);
            damages.add(d);
            finished = true;
        }
        if (distance >= spell.distance()) {
            finished = true;
        }

        if (distance > passed) {
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
