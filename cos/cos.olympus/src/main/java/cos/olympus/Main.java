package cos.olympus;

import cos.logging.Logger;
import cos.map.Land;
import cos.olympus.game.Direction;
import cos.olympus.game.Game;
import cos.olympus.game.GameMap;
import cos.olympus.game.server.GameServer;
import cos.olympus.ops.AnyOp;
import cos.olympus.ops.Login;
import cos.olympus.ops.Move;

import java.io.IOException;

import static cos.olympus.Util.tsm;
import static cos.olympus.game.Direction.NORTH;

class Main {

    private final static Logger logger = new Logger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {


        logger.info("Started ");

//        val bb = ByteBuffer.wrap(byteArrayOf(10, 20, 30, 40, 50, 60))
        var actionsBuffer = new DoubleBuffer<AnyOp>();
        var lands = Land.load("/Users/aleksandrsuslov/projects/mmo/cos/resources");
        var gameMap = new GameMap(lands);
        var game = new Game(gameMap, actionsBuffer);


        GameServer.run(actionsBuffer);
        actionsBuffer.add(new Login(1, 99));
        actionsBuffer.add(new Move(2, 99, 0, 0, NORTH, NORTH));

        var id = 0;
        while (true) {

            game.onTick(++id, tsm());
            //            logger.info("%d) %.3fms".format(id, execTime / 1000000.0))
            Thread.sleep(100);

        }

    }


}
