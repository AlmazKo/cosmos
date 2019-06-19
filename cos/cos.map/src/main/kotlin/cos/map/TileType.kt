package cos.map

enum class TileType(val id: Byte) {
    NOTHING(0),
    GRASS(1),
    SAND(2),
    LAVA(3),
    SHALLOW(4),
    DEEP_WATER(5),
    ICE(6),
    SNOW(7),
    ROAD(8);
}
