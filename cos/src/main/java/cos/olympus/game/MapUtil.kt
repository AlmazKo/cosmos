package cos.olympus.game

import cos.ops.Direction

object MapUtil {
    fun nextX(cr: Orientable): Int {
        val dir = if (cr.mv == null) cr.sight else cr.mv!!
        return nextX(cr, dir)
    }

    fun nextY(cr: Orientable): Int {
        val dir = if (cr.mv == null) cr.sight else cr.mv!!
        return nextY(cr, dir)
    }

    fun nextX(ort: Placeable, mv: Direction): Int {
        return when (mv) {
            Direction.NORTH, Direction.SOUTH -> ort.x
            Direction.WEST -> ort.x - 1
            Direction.EAST -> ort.x + 1
        }
    }

    fun nextY(ort: Placeable, mv: Direction): Int {
        return when (mv) {
            Direction.NORTH -> ort.y - 1
            Direction.SOUTH -> ort.y + 1
            Direction.WEST, Direction.EAST -> ort.y
        }
    }

    fun inZone(ort: Placeable, x: Int, y: Int, radius: Int): Boolean {
        val oX = ort.x
        val oY = ort.y
        return oX <= x + radius && x >= oX - radius && oY <= y + radius && oY >= y - radius
    }

    fun direction(from: Placeable, to: Placeable): Direction? {
        return if (from.y == to.y) {
            if (from.x < to.x) {
                Direction.EAST
            } else {
                Direction.WEST
            }
        } else if (from.x == to.x) {
            if (from.y < to.y) {
                Direction.SOUTH
            } else {
                Direction.NORTH
            }
        } else {
            null
        }
    }
}
