package cos.api;

import cos.logging.Logger;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.io.IOException;

public class Main {
    static Logger logger = Logger.get(Main.class);

    public static void main(String[] args) throws IOException {
        System.setProperty("user.timezone", "UTC");
        System.setProperty("vertx.disableDnsResolver", "true");
        logger.info("Starting...");
        var vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(Record.class, new RecordMessageCodec());
        vertx.deployVerticle(GameVerticle.class, new DeploymentOptions().setWorker(true));
        new Api(vertx);
    }
}
