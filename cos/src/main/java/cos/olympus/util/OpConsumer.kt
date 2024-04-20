package cos.olympus.util

import cos.logging.Logger
import cos.ops.SomeOp

fun interface OpConsumer {
    fun add(op: SomeOp)
    operator fun invoke(op: SomeOp) {
        //LOG.info(op, "out")
        add(op)
    }

    companion object {
        val LOG = Logger.get("OUT")
    }

}