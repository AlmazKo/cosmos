package cos.olympus.game.actions

import cos.olympus.Tsm
import cos.olympus.game.Creature
import cos.olympus.game.Direction
import cos.olympus.game.Duration

data class Step(
    override val x: Int,
    override val y: Int,
    override val time: Tsm,
    val direction: Direction,
    val duration: Duration,
    val creature: Creature,
    var distanceTravelled: Int = 0,
    var finished: Boolean = false
) : Action
