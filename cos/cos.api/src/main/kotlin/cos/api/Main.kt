package cos.api

import io.vertx.core.Vertx
import kotlinx.serialization.ImplicitReflectionSerializer

object Main {
    @ImplicitReflectionSerializer
    @JvmStatic
    fun main(args: Array<String>) {

        //            System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory")
        //            System.setProperty("user.timezone", "UTC")
        App(Vertx.vertx())

    }
}

