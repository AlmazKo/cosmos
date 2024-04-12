package cos.olympus.game

interface Placeable {
    val x: Int
    val y: Int

    companion object {
        @JvmStatic
        fun sample(x: Int, y: Int): Placeable {
            return object : Placeable {
                override val x = x
                override val y = y
            }
        }
    }
}
