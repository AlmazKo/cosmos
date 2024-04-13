package cos.olympus.game

import cos.ops.Direction

class Orientation(
    val actorId: Aid,
    override val x: Pos,
    override val y: Pos,
    override val speed: Int,
    override val offset: Int,
    override val sight: Direction,
    override val mv: Direction?
) : Orientable
