package cos.olympus.game.ops

import cos.olympus.Dir
import cos.olympus.Pos

class Move(
    override val id: Int,
    val userId: Int,
    val x: Pos,
    val y: Pos,
    val dir: Dir,
    val sight: Dir
) : AnyOp
