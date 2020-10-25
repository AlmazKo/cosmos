package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.olympus.game.Game;
import cos.olympus.game.GameMap;
import cos.olympus.game.server.GameServer;
import cos.ops.AnyOp;

import java.io.IOException;
import java.nio.file.Paths;

import static cos.olympus.Util.tsm;

public class Main {
    private final static int TICKS_PER_SECOND = 10;
    private final static Logger logger = new Logger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {


        logger.info("Started ");

//        val bb = ByteBuffer.wrap(byteArrayOf(10, 20, 30, 40, 50, 60))
        var requests = new DoubleBuffer<AnyOp>();
        var responses = new Responses();
        var lands = Land.load(Paths.get("", "../resources").toAbsolutePath());
        var gameMap = new GameMap(lands);
        var game = new Game(gameMap, requests);


        GameServer.run(requests, responses);
//        requests.add(new Login(1, 99));
//        requests.add(new Move(2, 99, 0, 0, NORTH, NORTH));

        var id = 0;
        while (true) {

            var oo = game.onTick(++id, tsm());
            if (!oo.isEmpty()) {
                logger.info("OOOPS");
                responses.ops.addAll(oo);
            }

            //            logger.info("%d) %.3fms".format(id, execTime / 1000000.0))
            Thread.sleep(1000 / TICKS_PER_SECOND);

        }

    }


    public static int toTickSpeed(int v) {
        return v / TICKS_PER_SECOND;
    }

}
