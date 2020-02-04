package cos.olympus.game.ops

import cos.olympus.Pos
import cos.olympus.game.Direction
import cos.olympus.game.IdDir

class StopMove(
    override val id: Int,
    val userId: Int,
    val x: Pos,
    val y: Pos,
    val dir: IdDir,
    val sight: IdDir
) : AnyOp
