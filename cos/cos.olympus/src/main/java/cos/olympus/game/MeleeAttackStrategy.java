package cos.olympus.game;

import cos.olympus.game.events.Damage;
import cos.olympus.game.events.MeleeAttack;
import cos.olympus.game.events.Spell;
import cos.ops.MeleeAttacked;
import cos.ops.OutOp;

import java.util.Collection;

import static cos.olympus.game.Util.nextX;
import static cos.olympus.game.Util.nextY;
import static cos.olympus.game.Util.rand;

public class MeleeAttackStrategy extends AbstractSpellStrategy {
    public final  MeleeAttack spell;
    private final World       world;
    public        int         targetX;
    public        int         targetY;

    public MeleeAttackStrategy(int tick, Creature cr, World world) {
        this.spell = new MeleeAttack(++SPELL_IDS, tick, cr.x(), cr.y(), cr.sight(), cr);
        this.world = world;
        this.targetX = nextX(cr, cr.sight);
        this.targetY = nextY(cr, cr.sight);
    }

    @Override public int id() {
        return spell.id();
    }

    public boolean onTick(int tick, Collection<OutOp> consumer, Collection<Damage> damages) {
        var victim = world.getCreature(targetX, targetY);
        if (victim != null && spell.source().id() != victim.id()) {
            boolean crit = rand(0, 10) == 1;
            int amount = crit ? rand(40, 60) : rand(10, 20);
            var d = new Damage(++DAMAGES_IDS, tick, victim, spell, amount, crit);
            logger.info("Damaged : " + d);
            damages.add(d);
        }
        finished = true;
        return true;
    }

    @Override public boolean inZone(Creature cr) {
        return Util.inZone(cr, targetX, targetY, 8);
    }

    @Override public Spell spell() {
        return spell;
    }

}
