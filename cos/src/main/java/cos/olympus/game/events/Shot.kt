package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.olympus.game.Pos
import cos.olympus.game.Speed
import cos.olympus.game.TickId
import cos.ops.Direction

data class Shot(
    override val id: Int,
    val x: Pos,
    val y: Pos,
    val speed: Speed,
    val dir: Direction,
    val distance: Int,
    override val tick: TickId,
    override val source: Actor
) : Spell, Event
