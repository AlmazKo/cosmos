package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.olympus.game.Game;
import cos.olympus.game.World;
import cos.olympus.game.server.Server;
import cos.olympus.game.server.Sessions;
import cos.olympus.util.OpsConsumer;
import cos.olympus.util.TimeUtil;

import java.io.IOException;
import java.nio.file.Paths;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

public class Main {

    private final static    Logger  logger      = Logger.get(Main.class);
    private volatile static boolean running     = true;
    private volatile static boolean appFinished = false;


    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Started");
        var res = (args.length > 0) ? Paths.get("", args[0]) : Paths.get("", "../../resources");

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

        var s = new Sessions();
        Server.run(s);

        var lands = Land.load(res.toAbsolutePath(), "map");
        var world = new World(lands);
        var game = new Game(world);

        TimeUtil.sleepUntil(100);
        var id = 0;
        long start;
        while (running) {
            start = nanoTime();
            var in = s.collect();
            var out = new OpsConsumer();
            game.onTick(++id, in, out);
            s.write(out);
//            logger.info("" + (nanoTime() - start) / 1000 + "us, in/out: " + in.size() + "/" + out.size());
            TimeUtil.sleepUntil(100);
        }
        appFinished = true;
        logger.info("Stopped");
    }
}
