package cos.api;

import cos.logging.Logger;
import cos.olympus.game.MetaGame;
import cos.olympus.util.OpsAggregator;
import cos.ops.ServiceOp;
import cos.ops.SomeOp;
import cos.ops.UserOp;
import cos.ops.out.UserPackage;
import io.vertx.core.AbstractVerticle;

import java.util.ArrayList;
import java.util.List;

import static cos.olympus.game.GameUtil.prepareGame;

public class GameVerticle extends AbstractVerticle {
    private final Logger log = Logger.get(getClass());

    private List<UserOp> userOps = new ArrayList<>();
    private List<ServiceOp> serviceOps = new ArrayList<>();
    private OpsAggregator out = new OpsAggregator();
    private MetaGame game;
    private int tick;
    private Bus bus;

    @Override
    public void start() throws Exception {
        log.info("Resources: " + System.getProperty("CosResourcesDir"));
        bus = new Bus(vertx.eventBus());
        game = prepareGame();

        var time = System.currentTimeMillis();
        var tick = time % 100;
        if (tick > 3) {
            Thread.sleep(100 - tick - 3);
        }
        var gt = new GameThread(game, this);
        var t = new Thread(gt);
        t.start();
        bus.consume("game_in", this::onMessage);
    }


    synchronized List<UserOp> extract() {
        if (userOps.isEmpty()) return List.of();

        var tmp = userOps;
        userOps = new ArrayList<>();
        return tmp;
    }


    void onReady(ArrayList<UserPackage> events, List<SomeOp> adminEvents) {
        /// todo check threads
        for (var e : events) {
            bus.publish("game_out", e);
        }

        for (var e : adminEvents) {
            bus.publish("game_admin_out", e);
        }
    }

    private void onMessage(Record record) {
        if (record instanceof UserOp u) {
//            log.info("<< " + record);
            userOps.add(u);
        } else {
            log.warn("Unknown op: " + record);
        }
    }
}
