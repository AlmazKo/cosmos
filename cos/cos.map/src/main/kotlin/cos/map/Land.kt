package cos.map

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


object Land {

    @JvmStatic
    fun load(): Lands {
        val parser = Json(JsonConfiguration.Default)

        cos.map.Json.parse(javaClass.getResource("/base1.json").readText()x)

        val rawTiles = parser.parseJson(javaClass.getResource("/base1.json").readText())
        val layers = parser.parseJson(javaClass.getResource("/map.json").readText())
        return MapParser.parse(layers.jsonObject, rawTiles.jsonObject)
    }
}
