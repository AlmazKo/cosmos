package cos.olympus.game.events

import cos.olympus.game.Actor
import cos.ops.Direction

data class Fireball(
    override val id: Int,
    val x: Int,
    val y: Int,
    val speed: Int,
    val dir: Direction,
    val distance: Int,
    override val tick: Int,
    override val source: Actor

) : Spell, Event
