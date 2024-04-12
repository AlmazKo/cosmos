package cos.olympus.game

import cos.logging.Logger
import cos.olympus.game.events.Fireball
import cos.olympus.game.events.Shot
import cos.olympus.game.events.Spell
import cos.olympus.game.strategy.FireballSpellStrategy
import cos.olympus.game.strategy.MeleeAttackStrategy
import cos.olympus.game.strategy.ShotSpellStrategy
import cos.olympus.game.strategy.SpellStrategy
import cos.olympus.util.OpConsumer
import cos.olympus.util.TimeUtil
import cos.ops.`in`.FireballEmmit
import cos.ops.`in`.MeleeAttack
import cos.ops.`in`.ShotEmmit
import cos.ops.out.FireballMoved
import cos.ops.out.MeleeAttacked
import cos.ops.out.ShotMoved

class Spells(private val world: World) {
    private val pause = TimeUtil.toTicks(1)

    private val spells = ArrayList<SpellStrategy>()

    fun onShot(tick: Int, op: ShotEmmit) {
        val cr = world.getCreature(op.userId) ?: return

        //todo validate cooldown
        if (tick - cr.lastSpellTick < pause) {
            logger.info("Ignore spell $op")
            return
        }

        cr.lastSpellTick = tick

        val spell = Shot(++SPELL_IDS, cr.x, cr.y, TimeUtil.toTickSpeed(1000), cr.sight, 10, tick, cr)
        val str = ShotSpellStrategy(spell, world)
        spells.add(str)
    }

    fun onSpell(tick: Int, op: FireballEmmit) {
        val cr = world.getCreature(op.userId) ?: return

        //todo validate cooldown
        if (tick - cr.lastSpellTick < pause) {
            logger.info("Ignore spell $op")
            return
        }

        cr.lastSpellTick = tick

        val spell = Fireball(++SPELL_IDS, cr.x, cr.y, TimeUtil.toTickSpeed(400), cr.sight, 8, tick, cr)
        val str = FireballSpellStrategy(spell, world)
        spells.add(str)
    }

    fun onMeleeAttack(tick: Int, op: MeleeAttack) {
        val cr = world.getCreature(op.userId) ?: return
        if (tick - cr.lastSpellTick < pause) {
            //too fast
            return
        }


        val spell = cos.olympus.game.events.MeleeAttack(++SPELL_IDS, tick, cr.x, cr.y, cr.sight, cr)
        val str = MeleeAttackStrategy(spell, world)
        spells.add(str)
    }

    fun onMeleeAttack(tick: Int, cr: Creature) {
        val spell = cos.olympus.game.events.MeleeAttack(++SPELL_IDS, tick, cr.x, cr.y, cr.sight, cr)
        val str = MeleeAttackStrategy(spell, world)
        spells.add(str)
    }

    fun onTick(tick: Int, damages: Damages, outOps: OpConsumer) {
        spells.forEach { it.onTick(tick, damages) }

        //notify
        spells.forEach { strategy ->
            val spell: Spell = strategy.spell
            world.allCreatures.forEach { cr ->
                if (strategy.inZone(cr)) {
                    if (cr.zoneSpells.put(strategy.id, strategy) == null) {
                        when (spell) {
                            is Fireball -> {
                                outOps.add(FireballMoved(SPELL_IDS++, tick, cr.id, spell.id, spell.x, spell.y, spell.speed, spell.dir, strategy.finished))
                            }

                            is cos.olympus.game.events.MeleeAttack -> {
                                outOps.add(MeleeAttacked(SPELL_IDS++, tick, cr.id, spell.id, spell.source.id))
                            }

                            is Shot -> {
                                outOps.add(ShotMoved(SPELL_IDS++, tick, cr.id, spell.id, spell.x, spell.y, spell.speed, spell.dir, strategy.finished))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onAfterTick() {
        spells.removeIf { obj: SpellStrategy -> obj.finished }
    }

    companion object {
        private val logger: Logger = Logger.get(Spells::class.java)

        protected var SPELL_IDS: Int = 0
    }
}
