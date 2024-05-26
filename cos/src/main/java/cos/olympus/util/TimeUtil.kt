package cos.olympus.util

object TimeUtil {
    const val REFRESH_TIME = 100L
    const val TICKS_PER_SECOND: Int = 1000 / REFRESH_TIME.toInt()


    init {
        assert(REFRESH_TIME > 0)
        assert(TICKS_PER_SECOND > 0)
    }

    fun toTickSpeed(v: Int): Int {
        return v / TICKS_PER_SECOND
    }

    fun toTicks(sec: Int): Int {
        return sec * TICKS_PER_SECOND
    }
}

