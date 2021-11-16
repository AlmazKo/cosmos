package cos.olympus.game;

import cos.olympus.game.events.Damage;
import cos.olympus.game.events.MeleeAttack;
import cos.olympus.game.events.Spell;

import java.util.Collection;

import static cos.olympus.Util.rand;
import static cos.olympus.game.MapUtil.nextX;
import static cos.olympus.game.MapUtil.nextY;

public class MeleeAttackStrategy extends AbstractSpellStrategy {
    public final MeleeAttack spell;
    private final World world;
    public int targetX;
    public int targetY;

    public MeleeAttackStrategy(MeleeAttack spell, World world) {
        this.world = world;
        this.spell = spell;
        var cr = spell.source();
        this.targetX = nextX(cr, cr.sight);
        this.targetY = nextY(cr, cr.sight);
    }

    @Override public int id() {
        return spell.id();
    }

    public boolean onTick(int tick, Collection<Damage> damages) {
        var victim = world.getCreature(targetX, targetY);
        if (victim != null && spell.source().id() != victim.id()) {
            boolean crit = rand(0, 10) == 1;
            int amount = crit ? rand(40, 60) : rand(10, 20);
            var coef = -0.1 + Math.pow(1.5, spell.source().metrics.lvl);
            var d = new Damage(++DAMAGES_IDS, tick, victim, spell, (int) (coef * amount), crit);
            logger.info(d);
            damages.add(d);
        }
        finished = true;
        return true;
    }

    @Override public boolean inZone(Creature cr) {
        return MapUtil.inZone(cr, targetX, targetY, 8);
    }

    @Override public Spell spell() {
        return spell;
    }

}
