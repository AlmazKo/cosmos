package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.olympus.game.Game;
import cos.olympus.game.World;
import cos.olympus.game.server.GameServer;
import cos.olympus.game.server.Sessions;
import cos.olympus.util.DoubleBuffer;
import cos.olympus.util.OpsConsumer;
import cos.ops.AnyOp;

import java.io.IOException;
import java.nio.file.Paths;

import static cos.olympus.Util.tsm;
import static java.lang.System.nanoTime;

public class Main {
    private final static int    TICKS_PER_SECOND = 10;
    private final static Logger logger           = new Logger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        logger.info("Started ");

        var res = (args.length > 0) ? Paths.get("", args[0]) : Paths.get("", "../../resources");
//        var requests = new DoubleBuffer<AnyOp>();
        var s = new Sessions();
        GameServer.run(s);

        var lands = Land.load(res.toAbsolutePath());
        var world = new World(lands);
        var game = new Game(world);


        var id = 0;
        long start;
        //noinspection InfiniteLoopStatement
        while (true) {
            start = nanoTime();
            var in = s.collect();
            var out = new OpsConsumer();
            game.onTick(++id, in, out);
            s.write(out);

            long execTime = nanoTime() - start;
//            long pause = 100_000_000 - execTime;
            logger.info("" + execTime / 1000 + "us");
//            if (pause > 1) Thread.sleep(pause);
            Thread.sleep(100);

        }

    }


    public static int toTickSpeed(int v) {
        return v / TICKS_PER_SECOND;
    }

    public static int toTicks(int sec) {
        return sec * TICKS_PER_SECOND;
    }

}
