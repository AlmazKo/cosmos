package cos.olympus.game;

import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Fireball;
import cos.olympus.game.events.Spell;

import java.util.Collection;

public class FireballSpellStrategy extends AbstractSpellStrategy {

    public final  Fireball spell;
    private final World    world;

    private int passed;
    public  int x;
    public  int y;

    public FireballSpellStrategy(Fireball spell, World world) {
        this.world = world;
        this.spell = spell;
        this.x = spell.x();
        this.y = spell.y();
    }

    @Override public int id() {
        return spell.id();
    }

    public boolean onTick(int tick, Collection<Damage> damages) {

        int distance = (tick - spell.tickId()) * spell.speed() / 100;

        x = spell.x();
        y = spell.y();

        switch (spell.dir()) {
            case NORTH -> y -= distance;
            case EAST -> x += distance;
            case SOUTH -> y += distance;
            case WEST -> x -= distance;
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

        logger.info("Spell distance: " + this);
        return finished;
    }

    @Override public boolean inZone(Creature cr) {
        return Util.inZone(cr, x, y, 8);
    }

    @Override public Spell spell() {
        return spell;
    }

    @Override public String toString() {
        return "FireballSpellStrategy{" +
                "passed=" + passed +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

