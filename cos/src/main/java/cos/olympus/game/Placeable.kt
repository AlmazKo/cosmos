package cos.olympus.game

interface Placeable {
    val x: Pos
    val y: Pos

    companion object {
        @JvmStatic
        fun sample(x: Pos, y: Pos): Placeable {
            return object : Placeable {
                override val x = x
                override val y = y
            }
        }
    }
}
