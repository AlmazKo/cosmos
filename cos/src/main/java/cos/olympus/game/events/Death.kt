package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.ops.out.Death

data class Death(
    override val id: Int,
    override val tick: Int,
    val spell: Spell,
    val victim: Actor
) : Event {
    fun toUserOp(userId: Int) = Death(id, userId, spell.source.id, victim.id)
}

