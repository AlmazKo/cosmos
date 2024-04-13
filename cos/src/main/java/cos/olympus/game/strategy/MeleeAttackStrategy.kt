package cos.olympus.game.strategy

import cos.olympus.Util
import cos.olympus.game.Actor
import cos.olympus.game.Damages
import cos.olympus.game.MapUtil.inZone
import cos.olympus.game.MapUtil.nextX
import cos.olympus.game.MapUtil.nextY
import cos.olympus.game.World
import cos.olympus.game.events.MeleeAttack
import kotlin.math.pow

class MeleeAttackStrategy(
    override val spell: MeleeAttack,
    private val world: World
) : SpellStrategy {
    private val targetX: Int
    private val targetY: Int
    override var finished = false

    init {
        val actor = spell.source
        this.targetX = nextX(actor, actor.sight)
        this.targetY = nextY(actor, actor.sight)
    }

    override val id = spell.id


    override fun onTick(tick: Int, damages: Damages): Boolean {
        val victim = world.getActor(targetX, targetY)
        if (victim != null && spell.source.id != victim.id) {
            val crit = Util.rand(0, 10) == 1
            val amount = if (crit) Util.rand(40, 60) else Util.rand(10, 20)
            val coef: Double = -0.1 + 1.5.pow(spell.source.metrics.lvl.toDouble())
            damages.on(victim, spell, (coef * amount).toInt(), crit)
        }
        finished = true
        return true
    }

    override fun inZone(actor: Actor): Boolean {
        return inZone(actor, targetX, targetY, 8)
    }

}
