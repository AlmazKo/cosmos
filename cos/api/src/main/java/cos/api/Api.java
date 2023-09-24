package cos.api;


import cos.logging.Logger;
import cos.map.Land;
import cos.map.Lands;
import cos.ops.out.UserPackage;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

class Api {
    private final Map<Integer, PlayerSession> sessions = new HashMap<>();
    private final Map<Integer, AdminSession> adminSessions = new HashMap<>();
    private final Logger log = Logger.get(getClass());
    private final AtomicInteger playerInc = new AtomicInteger(0);
    private final AtomicInteger adminInc = new AtomicInteger(0);
    private final Bus bus;


    Api(Vertx vertx) throws IOException {
        log.info("Vertx started!");
        this.bus = new Bus(vertx.eventBus());

        var dir = System.getProperty("CosResourcesDir");
        var res = (dir == null || dir.isBlank()) ? Paths.get("", "../../resources") : Paths.get("", dir);
        var lands = Land.load(res.toAbsolutePath(), "map");
        var lands2 = Land.load(res.toAbsolutePath(), "map_mike");
        var opts = new HttpServerOptions();

        opts.setHost("0.0.0.0");
        opts.setUseAlpn(true);
        opts.setSsl(true);
        opts.setPort(443);

        //https://www.process-one.net/blog/using-a-local-development-trusted-ca-on-macos/
        var sertOpts = new PemKeyCertOptions();
        sertOpts.setKeyValue(readRescourceToBuffer("/localhost+2-key.pem"));
        sertOpts.setCertValue(readRescourceToBuffer("/localhost+2.pem"));
        opts.setPemKeyCertOptions(sertOpts);

        var server = vertx.createHttpServer(opts);
        initApi(vertx, Map.of("map", lands, "map_mike", lands2), server);
        server.listen(handler -> {
            if (handler.failed()) {
                log.warn("Fail!", handler.cause());
                vertx.close();
            } else {
                log.info("Started!");
            }
        });

        bus.consume("game_out", this::onGameOut);
        bus.consume("game_admin_out", this::onGameAdminOut);
    }

    private void onGameAdminOut(Record record) {
        adminSessions.values().forEach(it -> it.onOp(record));
    }

    private void onGameOut(Record record) {
        if (record instanceof UserPackage pkg) {
            var sess = sessions.get(pkg.userId());
            if (sess == null) {
                log.warn("Empty session for user " + pkg.userId());
                return;
            }
            sess.onOp(pkg);
        } else {
            log.warn("Unknown op: " + record);
        }

    }

    private String prop(String name) {
        return System.getProperty(name, System.getenv(name));
    }


    private void initApi(Vertx vertx, Map<String, Lands> lands, HttpServer server) {
        var router = Router.router(vertx);
        initCors(router);
        router.route().handler(new WebLogger());
        lands.forEach((n, l) -> initMapApi(router, l, n));
        router.route("/r/*").handler(StaticHandler.create("../../resources"));
        router.route("/ws").handler(ctx ->
                ctx.request().toWebSocket(wsAr -> {
                    if (wsAr.failed()) {
                        ctx.fail(500);
                        return;
                    }
                    var ws = wsAr.result();
                    var userId = playerInc.incrementAndGet();
                    sessions.put(userId, new PlayerSession(ws, userId, it -> bus.publish("game_in", it)));
                }));


        router.route("/ws/admin").handler(ctx ->
                ctx.request().toWebSocket(wsAr -> {
                    if (wsAr.failed()) {
                        ctx.fail(500);
                        return;
                    }
                    var ws = wsAr.result();
                    adminSessions.put(adminInc.decrementAndGet(), new AdminSession(lands, ws));
                }));
        server.requestHandler(router);
    }

    private void initMapApi(Router router, Lands lands, String name) {
        var cc = Splitter.split16(lands);
        var basis = cc.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.stream(e.getValue()).map(t -> {
                    if (t == null) {
                        //  log.warn("Wrong " + e.getKey());
                        return List.of();
                    } else {
                        return List.of(t.id(), t.type().getId());
                    }
                }).toList()));


        router.get("/map/" + name).handler(req -> {
            var x = parseInt(req.queryParam("x").get(0));
            var y = parseInt(req.queryParam("y").get(0));
            var t = basis.get(new Splitter.Coord<>(x, y));
            req.response().putHeader("content-type", "application/json; charset=utf-8");
            if (t == null) {
                req.fail(404);
            } else {
                req.response().end(t.toString());
            }
        });

        router.route().failureHandler(ctx -> ctx.response().setStatusCode(ctx.statusCode()).end());
    }

    private static void initCors(Router router) {
        var cors = CorsHandler.create("*");
        cors.allowedMethod(HttpMethod.GET);
        var headers = new HashSet<String>();
        headers.add("content-type");
        headers.add("origin");
        headers.add("content-accept");
        headers.add("x-client-time");
        cors.maxAgeSeconds(600);
        cors.allowedHeaders(headers);
        router.route().handler(cors);
    }

    private static Buffer readRescourceToBuffer(String file) throws IOException {
        byte[] data = Api.class.getResourceAsStream(file).readAllBytes();
        return Buffer.buffer(data);
    }
}
