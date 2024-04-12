package cos.olympus.util

import cos.ops.SomeOp

fun interface OpConsumer {
    fun add(op: SomeOp)
}
