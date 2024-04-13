package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.olympus.game.Pos
import cos.ops.Direction

@JvmRecord
data class MeleeAttack(
    override val id: Int,
    override val tick: Int,
    val x: Pos,
    val y: Pos,
    val dir: Direction,
    override val source: Actor
) : Spell, Event
