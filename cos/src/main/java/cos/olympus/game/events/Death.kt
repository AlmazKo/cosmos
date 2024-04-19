package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.ops.out.Death

class Death(
    override val id: Int,
    override val tick: Int,
    val spell: Spell,
    val victim: Actor
) : Event {
    fun toUserOp(userId: Int): Death {
        return Death(id, userId, spell.source.id, victim.id)
    }

    override fun toString() = "Death{id=$id, tick=$tick, victim=${victim.id}, spell=${spell.id}}"
}

