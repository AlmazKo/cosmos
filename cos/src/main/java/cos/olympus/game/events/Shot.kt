package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.olympus.game.Pos
import cos.ops.Direction

data class Shot(
    override val id: Int,
    val x: Pos,
    val y: Pos,
    val speed: Int,
    val dir: Direction,
    val distance: Int,
    override val tick: Int,
    override val source: Actor
) : Spell, Event
