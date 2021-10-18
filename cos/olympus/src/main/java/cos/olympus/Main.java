package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.map.Lands;
import cos.olympus.game.Game;
import cos.olympus.game.Router;
import cos.olympus.game.World;
import cos.olympus.game.api.Connection;
import cos.olympus.game.api.Connections;
import cos.olympus.util.OpsConsumer;
import cos.olympus.util.TimeUtil;
import cos.ops.AnyOp;
import cos.ops.InOp;
import cos.ops.Op;
import cos.ops.in.Login;
import cos.ops.in.Logout;
import fx.nio.ServerLauncher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.getenv;
import static java.lang.Thread.sleep;

public class Main {
    private final static Logger logger = Logger.get(Main.class);
    private volatile static boolean running = true;
    private volatile static boolean appFinished = false;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("SIGTERM");
            running = false;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!appFinished)
                Runtime.getRuntime().halt(143);
        }));
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Starting...");
        var game = prepareGame(args);
        var api = setupApi();
        startGame(game, api);
        appFinished = true;
        logger.info("Stopped");
    }

    @NotNull private static List<Game> prepareGame(String[] args) throws IOException {
        var lands = parseResources(args, "map");
        var lands2 = parseResources(args, "map_mike");
        return List.of(new Game(new World(lands)), new Game(new World(lands2)));
    }

    private static void startGame(List<Game> games, Connections pool) throws InterruptedException {
        TimeUtil.sleepUntil(100); //align
        logger.info("Ready!");
        var id = new AtomicInteger(0);
        var router = new Router();
//        long start;
        while (running) {
            id.incrementAndGet();
            var out = new OpsConsumer();

//            start = nanoTime();

            var mapOps = new ArrayList<InOp>();
            var mikeOps = new ArrayList<InOp>();

            for (AnyOp op : pool.collect()) {
                if (op.code() == Op.LOGIN) {
                    router.onLogin(id.get(), (Login) op);
                } else if (op.code() == Op.LOGOUT) {
                    router.onLogout(id.get(), (Logout) op);
                }

                var userWorld = router.getWorld(op.userId());
                if (userWorld.equals("map")) {
                    mapOps.add((InOp) op);
                } else if (userWorld.equals("mike")) {
                    mikeOps.add((InOp) op);
                }
            }

            games.get(0).onTick(id.get(), mapOps, out);
            games.get(1).onTick(id.get(), mikeOps, out);
            pool.write(id.get(), out);
//            logger.info("" + (nanoTime() - start) / 1000 + "us, in/out: " + in.size() + "/" + out.size());
            TimeUtil.sleepUntil(100);
        }
    }

    @NotNull private static Lands parseResources(String[] args, String name) throws IOException {
        var res = (args.length > 0) ? Paths.get("", args[0]) : Paths.get("", "../../resources");
        return Land.load(res.toAbsolutePath(), name);
    }

    public static Connections setupApi() {
        var connections = new Connections();
        var host = getenv().getOrDefault("FX_SERVER_HOST", "0.0.0.0");
        var port = Integer.parseInt(getenv().getOrDefault("FX_SERVER_PORT", "6666"));
        ServerLauncher.run(host, port, ch -> {
            var conn = new Connection(ch);
            connections.register(conn);
            logger.info("New connection: " + ch.getRemoteAddress());
            return conn;
        });

        return connections;
    }
}
