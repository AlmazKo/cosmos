package cos.api

import cos.logging.Logger
import cos.ops.AnyOp
import cos.ops.Appear
import cos.ops.Direction
import cos.ops.Disconnect
import cos.ops.Login
import cos.ops.Move
import cos.ops.ObjAppear
import cos.ops.Op
import cos.ops.StopMove
import cos.ops.Unknown
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonArray
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
                val dirId = js.getString("dir")
                var sightId = js.getString("sight")
                if (sightId == null) sightId = dirId;

                Move(
                    cid.incrementAndGet(),
                    userId,
                    js.getInteger("x"),
                    js.getInteger("y"),
                    Direction.valueOf(dirId),
                    Direction.valueOf(sightId)
                )

            }
            "stop_move" -> {
                val sightId = js.getString("sight")
                StopMove(
                    cid.incrementAndGet(),
                    userId,
                    js.getInteger("x"),
                    js.getInteger("y"),
                    Direction.valueOf(sightId)
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
                    val messages = JsonArray()
                    try {
                        val buf = it.byteBuf.nioBuffer();
                        buf.rewind()

                        while (buf.hasRemaining()) {
                            val op = parse(buf)
                            log.info("Got Server response $op")
                            messages.add(JsonMapper.toJson(op))
                        }

                    } catch (e: Exception) {
                        log.warn("wrong op" + e.message)
                    }
                    val clientRes = JsonObject()
                        .put("tick", 1) //todo hardcode
                        .put("time", System.currentTimeMillis() / 1000)
                        .put("messages", messages)
//                    log.info("Sending ... $clientRes")
                    ws.writeTextMessage(clientRes.toString())
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
                Op.APPEAR -> Appear.read(b);
                Op.DISCONNECT -> Disconnect.read(b);
                Op.APPEAR_OBJ -> ObjAppear.read(b);
                else -> Unknown.read(b, len)
            }
        }
    }

}
