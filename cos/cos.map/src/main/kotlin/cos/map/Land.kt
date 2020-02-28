package cos.map

import cos.json.JsObject
import cos.json.Json2
import cos.json.Json3

object Land {

    @JvmStatic
    fun load(): Lands {

        val x =   Json3.parse("""{"trade": "https://login.primexbt.com/", "demo": "https://web.primexbt.com/", "api":"api.primexbt.com", "maintenance": true, "enable_recaptcha": true, "production": true, "xdStorageUrl": "https://primexbt.com/xd-storage"}""")

        val base = Json2.parse(javaClass.getResource("/base1.json").readText()) as JsObject
        val map = Json2.parse(javaClass.getResource("/map.json").readText()) as JsObject
        return MapParser.parse(map, base)
    }
}
