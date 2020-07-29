package cos.api


import cos.logging.Logger
import cos.map.Land
import cos.map.Lands
import cos.map.TileType
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.net.NetClientOptions
import io.vertx.core.net.NetSocket
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import kotlinx.serialization.ImplicitReflectionSerializer
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@ImplicitReflectionSerializer
class App(val vertx: Vertx) {
    var cid = AtomicInteger(0)
    private var socket: NetSocket? = null
    private lateinit var test: ShortArray

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
        setupClient()
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

    private fun setupClient() {

        val options = NetClientOptions()/*.setConnectTimeout(1000)*/
        val client = vertx.createNetClient(options)
        client.connect(6666, "localhost") { res ->
            if (res.succeeded()) {

                println("Connected!")
                socket = res.result()

                socket!!.handler {
                    log.info("Got response " + it)
                }
                socket!!.closeHandler {
                    log.info("Closed " + it)
                }
                socket!!.exceptionHandler {
                    log.warn("exceptionHandler " + it, it)
                }

                //                vertx.setPeriodic(3000) { _ ->
                //                    socket.write(moveOp(3, ++id, 99, 0, 0, 1, 2))
                //                }
            } else {
                println("Failed to connect: " + res.cause())
            }
        }
        //        val client2 = vertx.createNetClient(options);
        //        client2.connect(6666, "localhost") { res ->
        //            if (res.succeeded()) {
        //                println("Connected2!");
        //                val socket = res.result();
        //                socket.write(op(1, 88))
        //            } else {
        //                println("Failed to connect2: " + res.cause());
        //            }
        //        }
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
        router.route().handler(LoggerHandler.create(LoggerFormat.SHORT))

        initCors(router)


        val t = lands.basis.asSequence()
            .map { tileId ->
                val typeId = lands.tiles[tileId.toInt()]?.type?.id ?: TileType.NOTHING.id
                JsonArray(listOf(tileId, typeId))
            }
            .toList()

        val tiles = JsonArray(t)


        val cc = Splitter.split16(lands)
        val maps = cc.mapValues { (k, v) ->
            //            print(k)x
            //            println( v.joinToString())
            JsonArray(v.map { t ->
                if (t == null) {
                    println("Wrong $k - " + k)
                } else {

                    listOf(t.id, t.type.id)
                }
            })
        }

        //        var x = lands.offsetX
        //        var y = lands.offsetY
        //
        //
        //        for ((i, basis) in lands.basis.withIndex()) {
        //
        //        }


        //        lands.basis.asSequence().partition {  }

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
            val id = playerInc.incrementAndGet()
            log.info("Connected player: #$id")

            socket?.write(op(1, cid.incrementAndGet(), id))
            //            val p = map.addPlayer(id)
            //            PlayerSession(p, ws, game)
        }

        /*
        router.get("/map-piece").handler { req ->

            val x = req.queryParam("x").first().toInt()
            val y = req.queryParam("y").first().toInt()
            val data = ByteArray(60 * 60, { (it % 127).toByte() })
            val vp = MapBasalPiece(width = lands.width, height = lands.height, x = lands.offsetX, y = lands.offsetY, data = data)
            req.response().putHeader("content-type", "application/json; charset=utf-8")
            req.response()
                .end(tiles.toString())
        }*/

        //        router.get("/tiles").handler { req ->
        //
        //            val data = lands.tiles
        //                .filterNotNull()
        //                .map { JsonObject().put("id", it.id).put("type", it.type) }
        //                .toJson()
        //
        //            req.response().putHeader("content-type", "application/json; charset=utf-8")
        //            req.response().end(
        //                JsonObject()
        //                    .put("columns", 23)
        //                    .put("height", 32)
        //                    .put("data", data)
        //                    .toString()
        //            )
        //        }

        server.requestHandler(router::accept)
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

}
