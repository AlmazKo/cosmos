package cos.map

data class Tile(val id: Int, val type: TileType) {
    override fun toString() = "$type($id)"
}
