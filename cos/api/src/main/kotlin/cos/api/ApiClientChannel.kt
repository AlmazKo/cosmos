package cos.api

import cos.logging.Logger
import cos.ops.Registry
import cos.ops.out.UserPackage
import cos.ops.parser.ByteReader
import cos.ops.parser.ByteWriter
import cos.ops.parser.OpType
import fx.nio.ReadChannel
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.atomic.AtomicInteger

class ApiClientChannel(
    private val ch: SocketChannel
) : ReadChannel, AutoCloseable {

    private val logger = Logger.get(javaClass)
    private val SEQ = AtomicInteger(0)
    private lateinit var consumer: (pkg: UserPackage) -> Unit
    private val input = ByteBuffer.allocateDirect(16 * 1024)!!
    private val output = ByteBuffer.allocateDirect(16 * 1024)!!
    private var writer = ByteWriter(Registry.PARSER, output)
    private var reader = ByteReader(Registry.PARSER, ::onData)


    fun start(consumer: (pkg: UserPackage) -> Unit) {
        this.consumer = consumer
    }

    private fun onData(op: Any, seqId: Int, t: OpType) {
        if (op is Array<*>) {
            logger.info("<< #$seqId $t ${op.size} ops")
        } else if (op is UserPackage) {
//            if (op.userId() < 1000)
//                logger.info("<< #$seqId $t UserPackage[tick=${op.tick()},user=${op.userId()}, ops=${op.ops().size}]")
            consumer(op)
        } else {
            logger.info("<< #$seqId $t $op")
        }
    }

    override fun read() {
        var read = try {
            ch.read(input)
        } catch (e: IOException) {
            logger.error("Failed to read", e)
            close()
            return
        }
        if (read == -1) {
            close()
            return
        }

        reader.read(input)
        input.limit(input.capacity())
    }

    fun write(op: Record, type: OpType): Int {
        val seqId = SEQ.incrementAndGet()
        logger.info(">> #$seqId $op")
        writer.write(op, seqId, type)

        try {
            moveToChannel()
        } catch (e: IOException) {
            logger.error("Failed to write: $op", e)
        }

        return seqId
    }

    @Throws(IOException::class)
    fun moveToChannel() {
        if (!ch.isOpen) return

        if (output.position() > 0) {
            output.flip()
            val size = output.remaining()
            val written = ch.write(output)
            //todo move from sync
            if (written < size) {
                // log.warn("Full size wasn't written")
            }
            output.clear()
            //            log.info("Written: $written")
        }
    }


    override fun close() {
        if (ch.isOpen) {
            try {
                ch.close()
                logger.info("Closed")
            } catch (e: IOException) {
                // log.warn("Fail during closing ", e)
            }
        }
    }
}
