package cos.olympus.game

import cos.olympus.game.Units.METER
import cos.ops.Direction
import cos.ops.Direction.EAST
import cos.ops.Direction.NORTH
import cos.ops.Direction.SOUTH
import cos.ops.Direction.WEST

interface Orientable : Placeable {
    val speed: Int
    val offset: Int
    val mv: Direction?
    val sight: Direction

    fun ry(): Float {
        if (mv == NORTH) return y - (offset.toFloat() / METER)
        if (mv == SOUTH) return y + (offset.toFloat() / METER)
        return y.toFloat()
    }


    fun rx(): Float {
        if (mv == WEST) return x - (offset.toFloat() / METER)
        if (mv == EAST) return x + (offset.toFloat() / METER)
        return x.toFloat()
    }
}
