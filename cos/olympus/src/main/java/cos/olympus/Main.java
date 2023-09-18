package cos.olympus;

import cos.logging.Logger;
import cos.olympus.game.MetaGame;
import cos.olympus.game.api.Connections;
import cos.olympus.util.OpsConsumer;
import cos.olympus.util.TimeUtil;
import cos.ops.SomeOp;

import java.io.IOException;
import java.util.List;

import static cos.olympus.GameUtil.prepareGame;
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
        logger.info("Starting... " + System.getProperty("CosResourcesDir"));
        var metaGame = prepareGame();
        var api = setupApi();
        startGame(metaGame, api);
        appFinished = true;
        logger.info("Stopped");
    }

    public static void startGame(MetaGame game, Connections pool) throws InterruptedException {
        TimeUtil.sleepUntil(100); //align
        logger.info("Ready!");
        int tick = 0;
        List<SomeOp> serviceOps = List.of();

        while (running) {
            ++tick;
            var out = new OpsConsumer();
            game.onTick(tick, pool.collect(), serviceOps, out);
            pool.write(tick, out);
            serviceOps = out.getServiceData();
            TimeUtil.sleepUntil(100);
        }
    }

//            logger.info("" + (nanoTime() - start) / 1000 + "us, in/out: " + in.size() + "/" + out.size());


    public static Connections setupApi() {
        var connections = new Connections();
//        var host = getenv().getOrDefault("FX_SERVER_HOST", "0.0.0.0");
//        var port = Integer.parseInt(getenv().getOrDefault("FX_SERVER_PORT", "6666"));
//        ServerLauncher.run(host, port, ch -> {
//            var conn = new Connection(ch);
//            connections.register(conn);
//            logger.info("New connection: " + ch.getRemoteAddress());
//            return conn;
//        });

        return connections;
    }
}
