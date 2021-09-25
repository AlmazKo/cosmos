package cos.api


import cos.logging.Logger
import cos.map.Land
import cos.map.Lands
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class App(val vertx: Vertx) {
    var cid = AtomicInteger(0)
    private val log = Logger(javaClass)
    private val playerInc = AtomicInteger(0)

    init {
        log.info("Vertx started!")
        val lands = Land.load(Paths.get("", "../../resources").toAbsolutePath())

        val opts = HttpServerOptions().apply {
            host = "0.0.0.0"
            isUseAlpn = true
            isSsl = true
            port = 443
            //https://www.process-one.net/blog/using-a-local-development-trusted-ca-on-macos/
            pemKeyCertOptions = PemKeyCertOptions().apply {
                keyPath = "localhost+2-key.pem"
                certPath = "localhost+2.pem"
            }
        }

        val server = vertx.createHttpServer(opts)
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


    private fun initApi(vertx: Vertx, lands: Lands, server: HttpServer) {
        val router = Router.router(vertx)
        //        router.route().handler(WebLogger())
        initCors(router)

        //
        //        val t = lands.basis.asSequence()
        //            .map { tileId ->
        //                val typeId = lands.tiles[tileId.toInt()]?.type?.id ?: TileType.NOTHING.id
        //                JsonArray(listOf(tileId, typeId))
        //            }
        //            .toList()

        val cc = Splitter.split16(lands)
        val basis = cc.mapValues { (k, v) ->
            JsonArray(v.map { t ->
                if (t == null) {
                    println("Wrong $k - $k")
                    emptyList()
                } else {
                    listOf(t.id(), t.type().id)
                }
            })
        }
        val cco = Splitter.splitObjects16(lands)
        val objects = cco.mapValues { (k, v) ->
            JsonArray(v.map { t ->
                if (t == null) {
                    emptyList()
                    //println("Wrong $k - $k")
                } else {
                    listOf(t.id(), t.type().id)
                }
            })
        }

        router.route("/r/*").handler(StaticHandler.create("../../resources"))

        router.get("/map").handler { req ->
            val key = req.queryParam("x")[0].toInt() to req.queryParam("y")[0].toInt()
            val t = basis[key]!!
            req.response().putHeader("content-type", "application/json; charset=utf-8")
            req.response()
                .end(t.toString())
        }

        router.get("/objects").handler { req ->
            val key = req.queryParam("x")[0].toInt() to req.queryParam("y")[0].toInt()
            val t = objects.getOrDefault(key, JsonArray())
            req.response().putHeader("content-type", "application/json; charset=utf-8")
            req.response()
                .end(t.toString())
        }

        router.route("/ws").handler { ctx ->
            val ws = ctx.request().upgrade()
            PlayerSession(vertx, ws, playerInc.incrementAndGet())
        }
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


    companion object {


    }

}
