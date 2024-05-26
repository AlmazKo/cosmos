package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.olympus.game.Pos
import cos.olympus.game.TickId
import cos.ops.Direction

data class MeleeAttack(
    override val id: Int,
    override val tick: TickId,
    val x: Pos,
    val y: Pos,
    val dir: Direction,
    override val source: Actor
) : Spell, Event
