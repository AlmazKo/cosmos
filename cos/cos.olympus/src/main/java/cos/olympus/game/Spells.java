package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Fireball;
import cos.ops.FireballEmmit;
import cos.ops.FireballMoved;
import cos.ops.MeleeAttack;
import cos.ops.MeleeAttacked;
import cos.ops.OutOp;

import java.util.ArrayList;
import java.util.Collection;

import static cos.olympus.Main.toTickSpeed;
import static cos.olympus.Main.toTicks;

public class Spells {
    private final static Logger logger = new Logger(Spells.class);

    protected static int SPELL_IDS = 0;
    private          int pause     = toTicks(1);

    private final ArrayList<SpellStrategy> spells = new ArrayList<>();
    private final World                    world;

    public Spells(World world) {
        this.world = world;
    }

    void onSpell(int tick, FireballEmmit op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;
        //todo validate cooldown

        if (tick - cr.lastSpellTick < pause) {
            logger.info("Ignore spell " + op);
            return;
        }

        cr.lastSpellTick = tick;

        var spell = new Fireball(++SPELL_IDS, cr.x(), cr.y(), toTickSpeed(400), cr.sight(), 10, tick, cr);
        var str = new FireballSpellStrategy(spell, world);
        spells.add(str);
    }

    void onMeleeAttack(int tick, MeleeAttack op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;
        if (tick - cr.lastSpellTick < pause) {
            return;
        }


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
