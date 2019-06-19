package cos.map


data class TileResource(
    val id: Int,
    val type: TileType,
    val posX: Position,
    val posY: Position,
    val sx: Px,
    val sy: Px
) {
    fun toTile() = Tile(id, type)
}
