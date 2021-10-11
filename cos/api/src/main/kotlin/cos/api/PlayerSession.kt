package cos.api

import cos.logging.Logger
import cos.ops.AnyOp
import cos.ops.Direction
import cos.ops.OutOp
import cos.ops.`in`.*
import cos.ops.out.UserPackage
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.concurrent.atomic.AtomicInteger

class PlayerSession(
    private val ws: ServerWebSocket,
    private val userId: Int,
    private val olympus: (r: AnyOp) -> Int
) {
    private val cid = AtomicInteger(0)
    private val log = Logger.get(javaClass)

    @Volatile
    var isClosed = false

    init {
        log.info("Connected player: #$userId")
        setupClient()

        ws.closeHandler {
            isClosed = true
            send(Logout(cid.incrementAndGet(), userId))
            log.info("Client socket is closing ... ")
        }

        ws.textMessageHandler(::onRequest)
    }

    private fun send(op: AnyOp) {
        olympus(op)
    }

    private fun onRequest(msg: String) {
        val js = JsonObject(msg)
        val op = parseRequest(js) ?: return
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


    fun onOp(pkg: UserPackage) {
        val messages = JsonArray()
        pkg.ops().forEach {
            messages.add(JsonMapper.toJson(it as OutOp))
        }

        val clientRes = JsonObject()
            .put("tick", pkg.tick()) //todo hardcode
            .put("time", System.currentTimeMillis() / 1000)
            .put("messages", messages)
        ws.writeTextMessage(clientRes.toString())
    }

    private fun setupClient() {
        send(Login(cid.incrementAndGet(), userId))
    }
}
