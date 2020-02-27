package cos.map

import cos.json.Json2
import cos.json.JsObject

object Land {

    @JvmStatic
    fun load(): Lands {
        val base = Json2.parse(javaClass.getResource("/base1.json").readText()) as JsObject
        val map = Json2.parse(javaClass.getResource("/map.json").readText()) as JsObject
        return MapParser.parse(map, base)
    }
}
