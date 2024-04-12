package cos.olympus.game

import cos.ops.Direction

class Orientation(
    val creatureId: Int,
    override val x: Int,
    override val y: Int,
    override val speed: Int,
    override val offset: Int,
    override val sight: Direction,
    override val mv: Direction?
) : Orientable
