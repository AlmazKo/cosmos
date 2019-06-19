package cos.olympus.game.actions

import cos.olympus.Tsm
import cos.olympus.game.Creature

data class Death(
    override val x: Int,
    override val y: Int,
    override val time: Tsm,
    val victim: Creature,
    val culprit: Creature
) : Action {
    constructor(d: Damage) : this(d.x, d.y, d.time, d.victim, d.culprit)
}
