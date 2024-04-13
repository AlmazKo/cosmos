package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.ops.Direction

@JvmRecord
data class MeleeAttack(
    override val id: Int,
    override val tick: Int,
    val x: Int,
    val y: Int,
    val dir: Direction,
    override val source: Actor
) : Spell, Event
