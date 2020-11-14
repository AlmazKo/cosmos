package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Fireball;
import cos.ops.OutOp;

import java.util.Collection;

public class FireballSpellStrategy extends AbstractSpellStrategy {

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
        if (victim != null && spell.source().id() != victim.id()) {
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

