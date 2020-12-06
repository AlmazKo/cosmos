package cos.api.dto

import io.vertx.core.buffer.Buffer

class MapBasalPiece(
    val x: Int,
    val y: Int,
    val width: Short,
    val height: Short,
    val tiles: IntArray
)
