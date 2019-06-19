package cos.map

class Lands(
    val width: Short,
    val height: Short,
    val offsetX: Int,
    val offsetY: Int,
    val basis: ShortArray,
    val objects: ShortArray,
    val tiles: Array<Tile?>
)
