package cos.olympus.game.events

import cos.olympus.game.Creature
import cos.ops.Direction

//    public static final boolean finished = ;
data class Shot(
    override val id: Int,
    val x: Int,
    val y: Int,
    val speed: Int,
    val dir: Direction,
    val distance: Int,
    override val tick: Int,
    override val source: Creature
) : Spell, Event
