package cos.olympus.game

import cos.map.Tile

class Obj(
    val id: Int,
    val tile: Tile,
    override val x: Int,
    override val y: Int
) : Placeable
