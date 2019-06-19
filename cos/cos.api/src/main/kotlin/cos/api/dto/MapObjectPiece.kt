package cos.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapObjectPiece(
    val x: Int,
    val y: Int,
    val width: Short,
    val heigt: Short,
    val terrain: ByteArray
)
