package cos.api


import cos.api.JsonMapper.toJson
import cos.logging.Logger
import cos.map.Land
import cos.map.Lands
import cos.map.TileType
import cos.ops.AnyOp
import cos.ops.Arrival
import cos.ops.Op
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonArray
import io.vertx.core.net.NetClientOptions
import io.vertx.core.net.NetSocket
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import kotlinx.serialization.ImplicitReflectionSerializer
import java.nio.ByteBuffer
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@ImplicitReflectionSerializer
class App(val vertx: Vertx) {
    var cid = AtomicInteger(0)
    private val log = Logger(javaClass)
    private val playerInc = AtomicInteger(0)

    init {
        val lands = Land.load(Paths.get("", "../resources").toAbsolutePath())

        val opts = HttpServerOptions().apply {
            isUseAlpn = true
            isSsl = true
            port = 443
            pemKeyCertOptions = PemKeyCertOptions().apply {
                keyPath = "localhost+2-key.pem"
                certPath = "localhost+2.pem"
            }
        }

        val server = vertx.createHttpServer(opts)
        //        setupClient()
        initApi(vertx, lands, server)

        server.listen {
            if (it.failed()) {
                log.warn("Fail!", it.cause())
                vertx.close()
            } else {
                log.info("Started!")
            }

        }
    }


    private fun op(code: Byte, id: Int, userId: Int, vararg bytes: Byte): Buffer {
        val bf = Buffer.buffer(bytes.size + 2 + 4)
        bf.appendByte(code)
        bf.appendInt(id)
        bf.appendInt(userId)
        bf.appendBytes(bytes)
        bf.appendByte(Byte.MAX_VALUE)
        return bf
    }

    private fun moveOp(code: Byte, id: Int, userId: Int, x: Int, y: Int, s: Byte, d: Byte): Buffer {
        val bf = Buffer.buffer(1 + 4 + 4 + 1 + 1 + 1 + 1 + 1)
        bf.appendByte(code)
        bf.appendInt(id)
        bf.appendInt(userId)
        bf.appendInt(x)
        bf.appendInt(y)
        bf.appendByte(s)
        bf.appendByte(d)
        bf.appendByte(Byte.MAX_VALUE)
        return bf
    }

    private fun initApi(vertx: Vertx, lands: Lands, server: HttpServer) {
        val router = Router.router(vertx)
        //        router.route().handler(WebLogger())
        initCors(router)


        val t = lands.basis.asSequence()
            .map { tileId ->
                val typeId = lands.tiles[tileId.toInt()]?.type?.id ?: TileType.NOTHING.id
                JsonArray(listOf(tileId, typeId))
            }
            .toList()

        val cc = Splitter.split16(lands)
        val maps = cc.mapValues { (k, v) ->
            JsonArray(v.map { t ->
                if (t == null) {
                    println("Wrong $k - " + k)
                } else {
                    listOf(t.id, t.type.id)
                }
            })
        }

        router.route("/res/*").handler(StaticHandler.create("../../resources"))

        router.get("/map").handler { req ->

            val key = req.queryParam("x")[0].toInt() to req.queryParam("y")[0].toInt()
            val t = maps[key]!!

            req.response().putHeader("content-type", "application/json; charset=utf-8")
            req.response()
                .end(t.toString())
        }

        router.route("/ws").handler { ctx ->
            val ws = ctx.request().upgrade()
            PlayerSession(ws, playerInc.incrementAndGet())
        }
        server.requestHandler(router::accept)
    }


    inner class PlayerSession(private val ws: ServerWebSocket, val id: Int) {
        private var socket: NetSocket? = null

        init {
            log.info("Connected player: #$id")
            setupClient()

            ws.closeHandler {
                log.info("Closing connect...")
                socket?.close()
            }
        }

        private fun onStart() {
            socket?.write(op(1, cid.incrementAndGet(), id))
        }

        private fun setupClient() {

            val options = NetClientOptions()/*.setConnectTimeout(1000)*/
            val client = vertx.createNetClient(options)
            client.connect(6666, "127.0.0.1") { res ->
                if (res.succeeded()) {

                    println("Connected to core!!")
                    socket = res.result()
                    onStart()

                    socket!!.handler {
                        try {
                            val buf = it.byteBuf.nioBuffer();
                            buf.rewind()
                            val op = parse(buf)
                            log.info("Got response $op")
                            ws.writeTextMessage(toJson(op).toString())
                        } catch (e: Exception) {
                            log.warn("wrong op", e)
                        }
                    }
                    socket!!.closeHandler {
                        log.info("Closed ")
                        onClose()
                    }
                    socket!!.exceptionHandler {
                        log.warn("exceptionHandler " + it, it)
                    }

                } else {
                    println("Failed to connect: " + res.cause())
                    onClose()
                }
            }

        }

        fun onClose() {
            log.info("Closing connect...")
            socket?.close()
            if (!ws.isClosed) ws.close()
        }
    }

    private fun initCors(router: Router) {
        val cors = CorsHandler.create("*")
        cors.allowedMethod(HttpMethod.GET)
        val headers = HashSet<String>()
        headers.add("content-type")
        headers.add("origin")
        headers.add("content-accept")
        headers.add("x-client-time")
        cors.maxAgeSeconds(600)
        cors.allowedHeaders(headers)
        router.route().handler(cors)
    }


    companion object {

        fun parse(b: ByteBuffer): AnyOp {
            return when (b.get()) {
                Op.APPEAR -> Arrival.create(b);
                else -> throw RuntimeException("Unknown op")
            }
        }


    }

}
