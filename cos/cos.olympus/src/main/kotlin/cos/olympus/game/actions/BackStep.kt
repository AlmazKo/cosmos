package cos.olympus.game.actions

import cos.olympus.Tsm
import cos.olympus.game.Creature
import cos.olympus.game.Direction
import cos.olympus.game.Duration

data class BackStep(
    override val x: Int,
    override val y: Int,
    override val time: Tsm,
    val direction: Direction,
    val duration: Duration,
    val creature: Creature,
    var finished: Boolean = false
) : Action {

    constructor(s: Step, time: Tsm, duration: Duration = s.duration / 2) : this(
        s.x, s.y, time, s.direction, duration, s.creature
    )
}
