package cos.api;

import io.vertx.core.Vertx;

import java.io.IOException;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting... " + Instant.now());
        System.setProperty("user.timezone", "UTC");
        new App(Vertx.vertx());
    }
}
