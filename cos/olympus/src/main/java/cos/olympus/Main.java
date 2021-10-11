package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.map.Lands;
import cos.olympus.game.Game;
import cos.olympus.game.World;
import cos.olympus.game.api.Connection;
import cos.olympus.game.api.Connections;
import cos.olympus.util.OpsConsumer;
import cos.olympus.util.TimeUtil;
import fx.nio.ServerLauncher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;

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
        Game game = prepareGame(args);
        var api = setupApi();
        startGame(game, api);
        appFinished = true;
        logger.info("Stopped");
    }

    @NotNull private static Game prepareGame(String[] args) throws IOException {
        var lands = parseResources(args);
        var world = new World(lands);
        return new Game(world);
    }

    private static void startGame(Game game, Connections pool) throws InterruptedException {
        TimeUtil.sleepUntil(100); //align
        logger.info("Ready!");
        var id = 0;
//        long start;
        while (running) {
//            start = nanoTime();
            var in = pool.collect();
            var out = new OpsConsumer();
            game.onTick(++id, in, out);
            pool.write(id, out);
//            logger.info("" + (nanoTime() - start) / 1000 + "us, in/out: " + in.size() + "/" + out.size());
            TimeUtil.sleepUntil(100);
        }
    }

    @NotNull private static Lands parseResources(String[] args) throws IOException {
        var res = (args.length > 0) ? Paths.get("", args[0]) : Paths.get("", "../../resources");
        return Land.load(res.toAbsolutePath(), "map");
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
