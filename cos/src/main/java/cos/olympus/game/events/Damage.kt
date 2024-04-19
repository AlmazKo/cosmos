package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.ops.out.Damage

data class Damage(
    override val id: Int,
    override val tick: Int,
    val victim: Actor,
    val spell: Spell,
    val amount: Int,
    val crit: Boolean
) : Event {
    fun toUserOp(userId: Int): Damage {
        return Damage(id, userId, spell.source.id, victim.id, amount, spell.id, crit)
    }

    override fun toString(): String {
        return "Damage{" +
            "id=" + id +
            ", tick=" + tick +
            ", victim=" + victim.id +
            ", spell=" + spell.id +
            ", amount=" + amount +
            '}'
    }
}

