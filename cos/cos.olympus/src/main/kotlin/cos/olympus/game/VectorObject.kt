package cos.olympus.game

interface VectorObject {
    val x: Int
    val y: Int
    val offset: Byte
    val speed: Byte
    val dir: Direction

    fun toLong(): Long {
        //todo: implement it
        return 0L
    }
}
