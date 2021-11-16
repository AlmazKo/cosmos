package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.map.Lands;
import cos.olympus.game.Game;
import cos.olympus.game.MetaGame;
import cos.olympus.game.Teleports;
import cos.olympus.game.World;
import cos.olympus.game.api.Connection;
import cos.olympus.game.api.Connections;
import cos.olympus.util.OpsConsumer;
import cos.olympus.util.TimeUtil;
import fx.nio.ServerLauncher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

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
        var games = prepareGame(args);
        var api = setupApi();

        var metaGame = new MetaGame(games);
        startGame(metaGame, api);
        appFinished = true;
        logger.info("Stopped");
    }

    @NotNull private static Map<String, Game> prepareGame(String[] args) throws IOException {
        var lands = parseResources(args, "map");
        var lands2 = parseResources(args, "map_mike");
        return Map.of(
                "map", new Game(new World(lands), new Teleports(null)),
                "map_mike", new Game(new World(lands2), new Teleports(null))
        );
    }

    private static void startGame(MetaGame game, Connections pool) throws InterruptedException {
        TimeUtil.sleepUntil(100); //align
        logger.info("Ready!");
        int tick = 0;

        while (running) {
            ++tick;
            var out = new OpsConsumer();
            game.onTick(tick, pool.collect(), out);
            pool.write(tick, out);
            TimeUtil.sleepUntil(100);
        }
    }

//            logger.info("" + (nanoTime() - start) / 1000 + "us, in/out: " + in.size() + "/" + out.size());

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
