package cos.api

import cos.logging.Logger
import cos.ops.AnyOp
import cos.ops.Arrival
import cos.ops.Direction
import cos.ops.Disconnect
import cos.ops.Login
import cos.ops.Move
import cos.ops.Op
import cos.ops.StopMove
import cos.ops.Unknown
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetClientOptions
import io.vertx.core.net.NetSocket
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class PlayerSession(
    private val vertx: Vertx,
    private val ws: ServerWebSocket,
    val userId: Int
) {


    private val cid = AtomicInteger(0)
    private var socket: NetSocket? = null
    private val log = Logger(javaClass)

    init {
        log.info("Connected player: #$userId")
        setupClient()

        ws.closeHandler {
            log.info("Client socket is closing ... ")
            socket?.close()
        }

        ws.textMessageHandler(::onRequest)
    }

    private fun onStart() {
        send(Login(cid.incrementAndGet(), userId))
    }


    private fun send(op: AnyOp) {
        socket?.write(serialize(op))
    }

    private fun onRequest(msg: String) {
        val js = JsonObject(msg)
        log.info("onRequest $msg")
        val op = parseRequest(js) ?: return

        log.info("Get op: $op")
        send(op)
    }

    private fun parseRequest(js: JsonObject): AnyOp? {
        return when (js.getString("op")) {
            "move" -> {
                val dirId = js.getInteger("dir")
                var sightId = js.getInteger("sight")
                if (sightId == 0) sightId = dirId;

                Move(
                    cid.incrementAndGet(),
                    userId,
                    js.getInteger("x"),
                    js.getInteger("y"),
                    Direction.values()[dirId - 1],
                    Direction.values()[sightId - 1]
                )

            }
            "stop_move" -> {
                val sightId = js.getInteger("sight")
                StopMove(
                    cid.incrementAndGet(),
                    userId,
                    js.getInteger("x"),
                    js.getInteger("y"),
                    Direction.values()[sightId - 1]
                )
            }
            else -> null
        }

    }

    private fun setupClient() {

        val options = NetClientOptions()
        val client = vertx.createNetClient(options)
        client.connect(6666, "127.0.0.1") { res ->
            if (res.succeeded()) {

                log.info("Connected to Olympus!!")
                socket = res.result()
                onStart()

                socket!!.handler {
                    try {
                        val buf = it.byteBuf.nioBuffer();
                        buf.rewind()
                        val op = parse(buf)
                        log.info("Got Server response $op")
                        val clientRes = JsonMapper.toJson(op).toString()
                        log.info("Sending ... $clientRes")
                        ws.writeTextMessage(clientRes)
                    } catch (e: Exception) {
                        log.warn("wrong op", e)
                    }
                }
                socket!!.closeHandler {
                    log.info("Server socket is closing ... ")
                    ws.close()
                }
                socket!!.exceptionHandler {
                    log.warn("exceptionHandler " + it, it)
                }

            } else {
                println("Failed to connect: " + res.cause())
                ws.close()
            }
        }
    }

    companion object {
        private fun serialize(op: AnyOp): Buffer {
            val bb = ByteBuffer.allocate(256)
            bb.put(op.code())
            val pos = bb.position();
            bb.position(pos + 1);
            op.write(bb)
            bb.put(pos, (bb.position() - 2).toByte());
            val bw = Arrays.copyOf(bb.array(), bb.position())
            return Buffer.buffer(bw);
        }

        fun parse(b: ByteBuffer): AnyOp {
            val code = b.get();
            val len = b.get();
            return when (code) {
                Op.APPEAR -> Arrival.read(b);
                Op.DISCONNECT -> Disconnect.read(b);
                else -> Unknown.read(b, len)
            }
        }
    }

}
