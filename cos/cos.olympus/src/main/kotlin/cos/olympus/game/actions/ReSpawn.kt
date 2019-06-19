package cos.olympus.game.actions

import cos.olympus.Tsm
import cos.olympus.game.Creature

data class ReSpawn(
    override val x: Int,
    override val y: Int,
    override val time: Tsm,
    val creature: Creature
) : Action {
    constructor(time: Tsm, c: Creature) : this(c.x, c.y, time, c)
}
