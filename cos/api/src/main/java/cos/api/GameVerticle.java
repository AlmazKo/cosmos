package cos.api;

import cos.logging.Logger;
import cos.olympus.game.MetaGame;
import cos.olympus.game.api.Connections;
import cos.olympus.util.OpsConsumer;
import cos.ops.UserOp;
import io.vertx.core.AbstractVerticle;

import java.util.List;

public class GameVerticle extends AbstractVerticle {
    private final Logger log = Logger.get(getClass());
    private final Connections events = new Connections();
    private MetaGame game;
    private int tick;
    private Bus bus;

    @Override
    public void start() throws Exception {
        log.info("Starting... " + System.getProperty("CosResourcesDir"));
        bus = new Bus(vertx.eventBus());
        this.game = cos.olympus.GameUtil.prepareGame();
        vertx.setPeriodic(100, this::onTick);
        bus.consume("game_in", this::onMessage);
    }

    private void onTick(Long aLong) {
        ++tick;
        var out = new OpsConsumer();
        game.onTick(tick, events.collect(), List.of(), out);
        events.write(tick, out);
        for (var e : events.out) {
            log.info(">> " + e);
            bus.publish("game_out", e);
        }
        events.out.clear();
    }

    private void onMessage(Record record) {
        if (record instanceof UserOp u) {
            log.info("<< " + record);
            events.in(u);
        } else {
            log.warn("Unknown op: " + record);
        }

    }
}
