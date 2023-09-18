package cos.olympus.game.strategy;

import cos.olympus.game.Creature;
import cos.olympus.game.Damages;
import cos.olympus.game.MapUtil;
import cos.olympus.game.World;
import cos.olympus.game.events.MeleeAttack;
import cos.olympus.game.events.Spell;

import static cos.olympus.Util.rand;
import static cos.olympus.game.MapUtil.nextX;
import static cos.olympus.game.MapUtil.nextY;

public class MeleeAttackStrategy extends AbstractSpellStrategy {
    public final MeleeAttack spell;
    private final World world;
    public final int targetX;
    public final int targetY;

    public MeleeAttackStrategy(MeleeAttack spell, World world) {
        this.world = world;
        this.spell = spell;
        var cr = spell.source();
        this.targetX = nextX(cr, cr.sight());
        this.targetY = nextY(cr, cr.sight());
    }

    @Override
    public int id() {
        return spell.id();
    }

    public boolean onTick(int tick, Damages damages) {
        var victim = world.getCreature(targetX, targetY);
        if (victim != null && spell.source().id() != victim.id()) {
            boolean crit = rand(0, 10) == 1;
            int amount = crit ? rand(40, 60) : rand(10, 20);
            var coef = -0.1 + Math.pow(1.5, spell.source().metrics().lvl);
            damages.on(victim, spell, (int) (coef * amount), crit);
        }
        finished = true;
        return true;
    }

    @Override
    public boolean inZone(Creature cr) {
        return MapUtil.inZone(cr, targetX, targetY, 8);
    }

    @Override
    public Spell spell() {
        return spell;
    }

}
