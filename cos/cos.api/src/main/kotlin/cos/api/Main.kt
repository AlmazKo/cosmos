package cos.api

import io.vertx.core.Vertx
import kotlinx.serialization.ImplicitReflectionSerializer

object Main {
    @ImplicitReflectionSerializer
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("user.timezone", "UTC")
        App(Vertx.vertx())
    }
}

