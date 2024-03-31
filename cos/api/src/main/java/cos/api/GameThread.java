package cos.api;

import cos.logging.Logger;
import cos.logging.ThreadContext;
import cos.olympus.game.MetaGame;
import cos.olympus.util.OpsAggregator;
import cos.ops.ServiceOp;
import cos.ops.UserOp;

import java.util.ArrayList;
import java.util.List;

public class GameThread implements Runnable {

    private final Logger log = Logger.get(getClass());
    private List<ServiceOp> serviceOps = new ArrayList<>();
    private final MetaGame game;
    private final GameVerticle gameVerticle;
    private int tick;
    private OpsAggregator out = new OpsAggregator();


    public GameThread(MetaGame game, GameVerticle gameVerticle) {
        this.game = game;
        this.gameVerticle = gameVerticle;
    }

    @Override
    public void run() {
        try {
            var startMs = System.currentTimeMillis();
            var nextMs = startMs + (100 - startMs % 100);
            waitUntil(nextMs);

            while (true) {
                ++tick;
                var tickTime = nextMs;
                ThreadContext.set("TICK", "#" + tick);
                List<UserOp> userOps = gameVerticle.extract();
                game.onTick(tick, userOps, serviceOps, out);

                var events = out.groupByUser(tick, tickTime);
                var adminEvents = out.adminOps();
                serviceOps = out.serviceOps();
                this.out = new OpsAggregator();

                gameVerticle.onReady(events, adminEvents);
                nextMs += 100L;
                waitUntil(nextMs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitUntil(long millis) throws InterruptedException {
        long now = System.currentTimeMillis();
        if (millis - now > 15) {
            Thread.sleep(millis - now - 13);
        }

        while (System.currentTimeMillis() < millis) {
            Thread.onSpinWait();
        }
    }
}
