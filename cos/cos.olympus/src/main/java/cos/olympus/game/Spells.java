package cos.olympus.game;

import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Fireball;
import cos.ops.FireballEmmit;
import cos.ops.FireballMoved;
import cos.ops.MeleeAttack;
import cos.ops.MeleeAttacked;
import cos.ops.OutOp;

import java.util.ArrayList;
import java.util.Collection;

public class Spells {

    protected static       int    SPELL_IDS   = 0;

    private final ArrayList<SpellStrategy> spells = new ArrayList<>();
    private final World                    world;

    public Spells(World world) {
        this.world = world;
    }

    void onSpell(int tick, FireballEmmit op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;
        //todo validate cooldown

        var spell = new Fireball(++SPELL_IDS, cr.x(), cr.y(), 40, cr.sight(), 10, tick, cr);
        var str = new FireballSpellStrategy(spell, world);
        spells.add(str);
    }

    void onMeleeAttack(int tick, MeleeAttack op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;


        var spell = new cos.olympus.game.events.MeleeAttack(++SPELL_IDS, tick, cr.x(), cr.y(), cr.sight(), cr);
        var str = new MeleeAttackStrategy(spell, world);
        spells.add(str);
    }

    public void onTick(int tick, ArrayList<Damage> damages, Collection<OutOp> outOps) {
        spells.forEach(s -> s.onTick(tick, damages));

        spells.forEach((SpellStrategy strategy) -> {
            var spell = strategy.spell();
            world.getAllCreatures().forEach(cr -> {
                if (strategy.inZone(cr)) {
                    if (cr.zoneSpells.put(strategy.id(), strategy) == null) {
                        if (spell instanceof Fireball s) {
                            outOps.add(new FireballMoved(SPELL_IDS++, tick, cr.id(), s.id(), s.x(), s.y(), s.speed(), s.dir(), strategy.isFinished()));
                        } else if (spell instanceof cos.olympus.game.events.MeleeAttack s) {
                            outOps.add(new MeleeAttacked(SPELL_IDS++, tick, cr.id(), s.id(), s.source().id()));
                        }
                    }
                }
            });
        });
    }

    public void onAfterTick() {
        spells.removeIf(SpellStrategy::isFinished);
    }
}
