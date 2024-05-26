package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.olympus.game.TickId
import cos.ops.out.Damage

data class Damage(
    override val id: Int,
    override val tick: TickId,
    val victim: Actor,
    val spell: Spell,
    val amount: Int,
    val crit: Boolean
) : Event {

    fun toUserOp(userId: Int) = Damage(id, userId, spell.source.id, victim.id, amount, spell.id, crit)
}

