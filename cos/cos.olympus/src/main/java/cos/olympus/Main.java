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

import static cos.olympus.util.TimeUtil.sleepUntil;
import static java.lang.System.nanoTime;

public class Main {

    private final static Logger logger = new Logger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Started");
        var res = (args.length > 0) ? Paths.get("", args[0]) : Paths.get("", "../../resources");
        var s = new Sessions();
        Server.run(s);

        var lands = Land.load(res.toAbsolutePath());
        var world = new World(lands);
        var game = new Game(world);

        TimeUtil.sleepUntil(100);
        var id = 0;
        long start;
        //noinspection InfiniteLoopStatement
        while (true) {
            start = nanoTime();
            var in = s.collect();
            var out = new OpsConsumer();
            game.onTick(++id, in, out);
            s.write(out);
//            logger.info("" + (nanoTime() - start) / 1000 + "us, in/out: " + in.size() + "/" + out.size());
            TimeUtil.sleepUntil(100);
        }
    }
}
