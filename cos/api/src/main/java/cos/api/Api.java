package cos.api;


import cos.logging.Logger;
import cos.map.Land;
import cos.map.Lands;
import cos.ops.parser.OpType;
import fx.nio.Client;
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
    private Map<Integer, PlayerSession> sessions  = new HashMap<>();
    private ApiClientChannel            olympus;
    private Logger                      log       = Logger.get(getClass());
    private AtomicInteger               playerInc = new AtomicInteger(0);

    Api(Vertx vertx) throws IOException {
        log.info("Vertx started!");

        var dir = System.getProperty("CosResourcesDir");
        var res = (dir == null || dir.isBlank()) ? Paths.get("", "../../resources") : Paths.get("", dir);

        var lands = Land.load(res.toAbsolutePath(), "map");
        var lands2 = Land.load(res.toAbsolutePath(), "map_mike");

        var opts = new HttpServerOptions();

        opts.setHost("0.0.0.0");
        opts.setUseAlpn(true);
        opts.setSsl(true);
        opts.setPort(443);


//        Api.class.getResource("localhost+2-key.pem");
        //https://www.process-one.net/blog/using-a-local-development-trusted-ca-on-macos/
        var pkco = new PemKeyCertOptions();
        pkco.setKeyPath(Api.class.getResource("localhost+2-key.pem").toString());
        pkco.setCertPath(Api.class.getResource("localhost+2-key.pem").toString());
        opts.setPemKeyCertOptions(pkco);


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
    }

    private void connectToOlympus() {
        Client.run("127.0.0.1", 6666, ch -> {
            this.olympus = new ApiClientChannel(ch);
            log.info("Connected to Olympus");
            olympus.start(pkg -> {
                sessions.values().removeIf(PlayerSession::isClosed);
                var sess = sessions.get(pkg.userId());
                if (sess != null) sess.onOp(pkg);
            });

            return this.olympus;
        });
    }

    private Buffer op(Byte code, int id, int userId, byte[] bytes) {
        var bf = Buffer.buffer(bytes.length + 2 + 4);
        bf.appendByte(code);
        bf.appendInt(id);
        bf.appendInt(userId);
        bf.appendBytes(bytes);
        bf.appendByte(Byte.MAX_VALUE);
        return bf;
    }


    private void initApi(Vertx vertx, Map<String, Lands> lands, HttpServer server) {
        connectToOlympus();
        var router = Router.router(vertx);
        initCors(router);
        router.route().handler(new WebLogger());
        lands.forEach((n, l) -> initMapApi(router, l, n));
        router.route("/r/*").handler(StaticHandler.create("../../resources"));
        router.route("/ws").handler(ctx ->
                ctx.request().toWebSocket(wsAr -> {
                    var ws = wsAr.result();//fixme handle error
                    var userId = playerInc.incrementAndGet();
                    sessions.put(userId, new PlayerSession(ws, userId, it -> olympus.write((Record) it, OpType.REQUEST)));
                }));
        server.requestHandler(router);
    }

    private void initMapApi(Router router, Lands lands, String name) {
        var cc = Splitter.split16(lands);
        var basis = cc.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.stream(e.getValue()).map(t -> {
                    if (t == null) {
                        log.warn("Wrong " + e.getKey());
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
                req.fail(400);
            } else {
                req.response().end(t.toString());
            }
        });
    }

    private void initCors(Router router) {
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
}
