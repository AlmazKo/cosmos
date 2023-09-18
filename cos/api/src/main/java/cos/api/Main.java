package cos.api;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.io.IOException;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting... " + Instant.now());
        System.setProperty("user.timezone", "UTC");
        var vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(Record.class, new RecordMessageCodec());
        vertx.deployVerticle(GameVerticle.class, new DeploymentOptions().setWorker(true));
        new Api(vertx);
    }
}
