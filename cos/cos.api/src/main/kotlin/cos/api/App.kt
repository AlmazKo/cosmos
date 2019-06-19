package cos.api


import cos.map.Land
import cos.map.Lands
import cos.map.TileType
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.JksOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@ImplicitReflectionSerializer
class App(vertx: Vertx) {
    private lateinit var test: ShortArray
    private val log = LoggerFactory.getLogger(javaClass)
    private val playerInc = AtomicInteger(0)

    init {
        val lands = Land.load()

        val opts = HttpServerOptions().apply {
            isUseAlpn = true
            isSsl = true
            port = 443
            keyStoreOptions = JksOptions().apply {
                path = "cos-test.ks"
                password = "123456"
            }
        }

        val server = vertx.createHttpServer(opts)

        initApi(vertx, lands, server)

        server.listen {
            if (it.failed()) {
                log.info("Fail!", it.cause())
                vertx.close()
            } else {
                log.info("Started!")
            }

        }
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
            //            print(k)
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
