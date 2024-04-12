package cos.olympus.game.events

import cos.olympus.game.Creature
import cos.ops.out.Death

class Death(
    override val id: Int,
    override val tick: Int,
    val spell: Spell,
    val victim: Creature
) : Event {
    fun toUserOp(userId: Int): Death {
        return Death(id, id, userId, spell.source.id, victim.id)
    }

    override fun toString() = "Death{id=$id, tick=$tick, victim=${victim.id}, spell=${spell.id}}"
}

