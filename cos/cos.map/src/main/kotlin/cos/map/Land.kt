package cos.map

import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.lang.System.currentTimeMillis


object Land {

    @JvmStatic
    fun load(): Lands {
        val parser = Json(JsonConfiguration.Default)
        val base = javaClass.getResource("/base1.json").readText();
        val map = javaClass.getResource("/map.json").readText();

        //        val b1 = cos.map.Json.parse(base)
        //        val m1 = cos.map.Json.parse(map)

        var vx: Any? = null
        var kx: Any? = null
        var j1: Any? = null
        var j2: Any? = null
        for (x in 1..1000) {
            vx = JsonObject(map)
            kx = parser.parseJson(map)
//            j1 = cos.map.Json.parse(map)
            j2 = cos.json.Json2.parse(map)
        }

        print("${vx.hashCode()} ${kx.hashCode()} $j1 ${j2.hashCode()}")


        var time = currentTimeMillis()

        for (x in 1..10000) {
            vx = JsonObject(map)
        }

        println("")
        println("vx " + (currentTimeMillis() - time))

        time = currentTimeMillis()

        for (x in 1..10000) {
            kx = parser.parseJson(map)
        }

        println("kx " + (currentTimeMillis() - time))
//
//        time = currentTimeMillis()
//
//        for (x in 1..1000) {
//            j1 = cos.map.Json.parse(map)
//        }
//
//        println("")
//        println("j1 " + (currentTimeMillis() - time))
//        println("")
//        time = currentTimeMillis()

        for (x in 1..10000) {
            j2 = cos.json.Json2.parse(map)
        }

        println("j2 " + (currentTimeMillis() - time))

        print("${vx.hashCode()} ${kx.hashCode()} $j1 ${j2.hashCode()}")


System.exit(0)


        val rawTiles = parser.parseJson(base)
        val layers = parser.parseJson(map)
        return MapParser.parse(layers.jsonObject, rawTiles.jsonObject)
    }
}
