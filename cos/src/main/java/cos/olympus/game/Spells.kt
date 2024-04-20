package cos.olympus.game

import cos.logging.Logger
import cos.olympus.game.events.Fireball
import cos.olympus.game.events.MeleeAttack
import cos.olympus.game.events.Shot
import cos.olympus.game.events.Spell
import cos.olympus.game.strategy.FireballSpellStrategy
import cos.olympus.game.strategy.MeleeAttackStrategy
import cos.olympus.game.strategy.ShotSpellStrategy
import cos.olympus.game.strategy.SpellStrategy
import cos.olympus.util.OpConsumer
import cos.olympus.util.TimeUtil
import cos.ops.`in`.FireballEmmit
import cos.ops.`in`.ShotEmmit

class Spells(private val world: World) : TickAware {
    private var tick: Int = 0
    private val pause = TimeUtil.toTicks(1)
    private val spells = ArrayList<SpellStrategy>()

    override fun onTick(tick: Int) {
        this.tick = tick
    }

    fun onShot(op: ShotEmmit) {
        val a = world.getActor(op.userId) ?: return

        //todo validate cooldown
        if (tick - a.lastSpellTick < pause) {
            logger.info("Ignore spell $op")
            return
        }

        a.lastSpellTick = tick

        val spell = Shot(++SPELL_IDS, a.x, a.y, TimeUtil.toTickSpeed(1000), a.sight, 10, tick, a)
        val str = ShotSpellStrategy(spell, world)
        spells.add(str)
    }

    fun onSpell(op: FireballEmmit) {
        val a = world.getActor(op.userId) ?: return

        //todo validate cooldown
        if (tick - a.lastSpellTick < pause) {
            logger.info("Ignore spell $op")
            return
        }

        a.lastSpellTick = tick

        val spell = Fireball(++SPELL_IDS, a.x, a.y, TimeUtil.toTickSpeed(400), a.sight, 8, tick, a)
        val str = FireballSpellStrategy(spell, world)
        spells.add(str)
    }

    fun onMeleeAttack(op: cos.ops.`in`.MeleeAttack) {
        val a = world.getActor(op.userId) ?: return
        if (tick - a.lastSpellTick < pause) {
            //too fast
            return
        }


        val spell = MeleeAttack(++SPELL_IDS, tick, a.x, a.y, a.sight, a)
        val str = MeleeAttackStrategy(spell, world)
        spells.add(str)
    }

    fun onMeleeAttack(a: Actor) {
        val spell = MeleeAttack(++SPELL_IDS, tick, a.x, a.y, a.sight, a)
        val str = MeleeAttackStrategy(spell, world)
        spells.add(str)
    }

    fun onTick(damages: Damages, outOps: OpConsumer) {
        spells.forEach { it.onTick(tick, damages) }

        //notify
        spells.forEach { strategy ->
            val spell: Spell = strategy.spell
            world.allActors.forEach { a ->
                if (strategy.inZone(a)) {
                    if (a.zoneSpells.put(strategy.id, strategy) == null) {
                        when (spell) {
                            is Fireball -> {
                                outOps.add(cos.ops.out.Fireball(SPELL_IDS++, tick, a.id, spell.id, spell.x, spell.y, spell.speed, spell.dir, strategy.finished))
                            }

                            is MeleeAttack -> {
                                outOps.add(cos.ops.out.MeleeAttack(SPELL_IDS++, a.id, spell.id, spell.source.id))
                            }

                            is Shot -> {
                                outOps.add(cos.ops.out.Shot(SPELL_IDS++, a.id, spell.id, spell.x, spell.y, spell.speed, spell.dir, strategy.finished))
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
