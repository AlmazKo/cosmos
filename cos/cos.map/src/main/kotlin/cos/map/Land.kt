package cos.map

import io.vertx.core.json.JsonObject

object Land {

    @JvmStatic
    fun load(): Lands {
        val rawTiles = JsonObject(javaClass.getResource("/base1.json").readText())
        val layers = JsonObject(javaClass.getResource("/map.json").readText())
        return MapParser.parse(layers, rawTiles)
    }
}
