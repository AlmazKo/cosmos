package cos.api

import cos.logging.Logger
import cos.ops.AnyOp
import cos.ops.Appear
import cos.ops.CreatureHid
import cos.ops.CreatureMoved
import cos.ops.Damage
import cos.ops.Death
import cos.ops.Direction
import cos.ops.Disconnect
import cos.ops.Exit
import cos.ops.FireballEmmit
import cos.ops.FireballMoved
import cos.ops.Login
import cos.ops.MeleeAttack
import cos.ops.MeleeAttacked
import cos.ops.Metrics
import cos.ops.Move
import cos.ops.ObjAppear
import cos.ops.Op
import cos.ops.OutOp
import cos.ops.ShotEmmit
import cos.ops.ShotMoved
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
        //   todo debug     log.info("onRequest $msg")
        val op = parseRequest(js) ?: return

        log.info("Get op: $op")
        send(op)
    }

    private fun parseRequest(js: JsonObject): AnyOp? {
        return when (js.getString("op")) {
            "move" -> {
                val dirId: String? = js.getString("dir")
                var sightId = js.getString("sight")
                if (sightId == null) sightId = dirId;
                if (dirId == null && sightId == null) throw IllegalArgumentException("Wrong move request")

                Move(
                    cid.incrementAndGet(),
                    userId,
                    js.getInteger("x"),
                    js.getInteger("y"),
                    if (dirId === null) null else Direction.valueOf(dirId),
                    Direction.valueOf(sightId)
                )
            }
            "emmit_fireball" -> {
                FireballEmmit(
                    cid.incrementAndGet(),
                    userId
                )
            }
            "emmit_shot" -> {
                ShotEmmit(
                    cid.incrementAndGet(),
                    userId
                )
            }
            "melee_attack" -> MeleeAttack(cid.incrementAndGet(), userId)
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
                onStart();

                val buf = ByteBuffer.allocate(32 * 1024)

                socket!!.handler {
                    val messages = JsonArray()
                    val tickId = try {
                        val bufOrigin = it.byteBuf.nioBuffer();
                        //println("Receive " + it.length() + " bytes")
                        bufOrigin.rewind()
                        buf.put(bufOrigin)
                        read(buf, messages)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        log.warn("wrong op " + e)
                    }
                    if (messages.isEmpty) return@handler

                    val clientRes = JsonObject()
                        .put("tick", tickId) //todo hardcode
                        .put("time", System.currentTimeMillis() / 1000)
                        .put("messages", messages)
                    //log.info("Sending ... ${messages.size()} ops")
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

    private fun read(buf: ByteBuffer, messages: JsonArray): Int {
        buf.flip()
        var tick = -1
        while (buf.hasRemaining()) {
            val op = parse(buf)
            // log.info("#$userId Got Server response $op, left: " + buf.remaining())
            if (op === null) {
                log.info("Left in msg " + buf.remaining() + " bytes")
                //                buf.compact();
                break;
            } else {
                tick = op.tick()
                if (op.userId() == userId) {
                    // log.info("#$userId Got Server response $op")
                    messages.add(JsonMapper.toJson(op))
                }
            }
        }
        buf.compact();
        return tick
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

        fun parse(b: ByteBuffer): OutOp? {
            b.mark()
            val code = b.get();
            val len = b.get();

            if (b.remaining() < len || code == 0.toByte()) {
                System.err.println("Unfinished opcode $code")
                b.reset()
                return null
            }

            return when (code) {
                Op.APPEAR -> Appear.read(b);
                Op.DISCONNECT -> Disconnect.read(b);
                Op.APPEAR_OBJ -> ObjAppear.read(b);
                Op.CREATURE_MOVED -> CreatureMoved.read(b);
                Op.CREATURE_HID -> CreatureHid.read(b);
                Op.FIREBALL_MOVED -> FireballMoved.read(b);
                Op.SHOT_MOVED -> ShotMoved.read(b);
                Op.MELEE_ATTACKED -> MeleeAttacked.read(b);
                Op.DAMAGE -> Damage.read(b);
                Op.DEATH -> Death.read(b);
                Op.EXIT -> Exit.read(b);
                Op.METRICS -> Metrics.read(b);
                else -> {
                    System.err.println("Unknown opcode: $code")
                    Unknown.read(b, len)
                }
            }
        }
    }

}
