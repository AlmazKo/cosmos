package cos.olympus.game.strategy;

import cos.olympus.Util;
import cos.olympus.game.Creature;
import cos.olympus.game.Damages;
import cos.olympus.game.MapUtil;
import cos.olympus.game.World;
import cos.olympus.game.events.Shot;
import cos.olympus.game.events.Spell;

public class ShotSpellStrategy extends AbstractSpellStrategy {

    public final Shot spell;
    private final World world;

    private int passed;
    public int x;
    public int y;

    public ShotSpellStrategy(Shot spell, World world) {
        this.world = world;
        this.spell = spell;
        this.x = spell.x();
        this.y = spell.y();
    }

    @Override
    public int id() {
        return spell.id();
    }

    public boolean onTick(int tick, Damages damages) {

        int distance = (tick - spell.tick()) * spell.speed() / 100;

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
            boolean crit = Util.rand(0, 5) == 1;
            damages.on(victim, spell, crit ? 200 : 100, crit);
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

    @Override
    public boolean inZone(Creature cr) {
        return MapUtil.inZone(cr, x, y, 8);
    }

    @Override
    public Spell spell() {
        return spell;
    }

    @Override
    public String toString() {
        return "ShotSpellStrategy{" +
                "passed=" + passed +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

