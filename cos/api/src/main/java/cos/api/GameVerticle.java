package cos.api;

import cos.logging.Logger;
import cos.olympus.game.MetaGame;
import cos.olympus.util.OpsAggregator;
import cos.ops.ServiceOp;
import cos.ops.UserOp;
import io.vertx.core.AbstractVerticle;

import java.util.ArrayList;
import java.util.List;

import static cos.olympus.GameUtil.prepareGame;

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
        vertx.setPeriodic(100, this::onTick);
        bus.consume("game_in", this::onMessage);
    }

    private void onTick(Long l) {
        ++tick;
        game.onTick(tick, userOps, serviceOps, out);
        var events = out.groupByUser(tick);
        for (var e : events) {
//            log.info(e, "out");
            bus.publish("game_out", e);
        }

        var adminEvents = out.adminOps();
        for (var e : adminEvents) {
//            log.info(e, "out_admin");
            bus.publish("game_admin_out", e);
        }
        serviceOps = this.out.serviceOps();
        userOps = new ArrayList<>();
        this.out = new OpsAggregator();
    }

    private void onMessage(Record record) {
        if (record instanceof UserOp u) {
            log.info("<< " + record);
            userOps.add(u);
        } else {
            log.warn("Unknown op: " + record);
        }
    }
}
