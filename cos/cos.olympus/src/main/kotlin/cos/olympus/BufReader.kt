package cos.olympus

import java.nio.ByteBuffer

class BufReader(private val buf: ByteBuffer) {
    private var pos: Int = 0

    fun current() = buf[pos]

    fun getByte(): Byte {
        val x = buf.get(pos)
        pos++
        return x
    }

    fun getInt(): Int {
        val x = buf.getInt(pos)
        pos += 4
        return x
    }

    fun hasData() = buf[pos] == NOPE

    companion object {
        const val NOPE: Byte = 0
    }


}
