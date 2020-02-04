package cos.olympus.game

class DoubleBuffer {

    private val first = ArrayList<Any>()
    private val second = ArrayList<Any>()
    private var current = first

    @Synchronized fun add(cmd: Any) {
        current.add(cmd)
    }

    //todo: use compareAndSet
    @Synchronized fun swapAndGet(): List<Any> {

        val result: ArrayList<Any>
        if (current === first) {
            current = second
            result = first
        } else {
            current = first
            result = second
        }

        current.clear()
        return result
    }
}
