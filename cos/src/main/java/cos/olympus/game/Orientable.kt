package cos.olympus.game

import cos.ops.Direction

interface Orientable : Placeable {
    val speed: Int
    val offset: Int
    val mv: Direction?
    val sight: Direction
}
