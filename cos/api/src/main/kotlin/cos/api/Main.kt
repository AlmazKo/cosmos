package cos.api

import io.vertx.core.Vertx
import java.time.Instant

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Starting... " + Instant.now())
        System.setProperty("user.timezone", "UTC")
        App(Vertx.vertx())
    }
}

