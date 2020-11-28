package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.olympus.game.Game;
import cos.olympus.game.World;
import cos.olympus.game.server.GameServer;
import cos.olympus.util.DoubleBuffer;
import cos.ops.AnyOp;

import java.io.IOException;
import java.nio.file.Paths;

import static cos.olympus.Util.tsm;

public class Main {
    private final static int    TICKS_PER_SECOND = 10;
    private final static Logger logger           = new Logger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        logger.info("Started ");

        var res = (args.length>0) ? Paths.get("", args[0]) : Paths.get("", "../../resources");
        var requests = new DoubleBuffer<AnyOp>();
        var responses = new Responses();
        var lands = Land.load(res.toAbsolutePath());
        var gameMap = new World(lands);
        var game = new Game(gameMap, requests);


        GameServer.run(requests, responses);
        var id = 0;
        while (true) {

            var oo = game.onTick(++id, tsm());
            if (!oo.isEmpty()) {
                //logger.info(oo.size() + " ops");
                responses.ops.addAll(oo);
            }

            //            logger.info("%d) %.3fms".format(id, execTime / 1000000.0))
            Thread.sleep(1000 / TICKS_PER_SECOND);

        }

    }


    public static int toTickSpeed(int v) {
        return v / TICKS_PER_SECOND;
    }

    public static int toTicks(int sec) {
        return sec * TICKS_PER_SECOND;
    }

}
