package cos.map

import cos.map.TileType.NOTHING
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object MapParser {

    private const val chunkSize = 16;

    class Spec(
        val width: Int,
        val height: Int,
        val shiftX: Int,
        val shiftY: Int
    )


    fun parse(rawMap: JsonObject, rawTiles: JsonObject): Lands {

        val layers = rawMap.getArray("layers")
        val spec = calcSpec(layers)
        val map = readChunks(layers.getObject(0).getArray("chunks"), spec)
        val objects = readChunks(layers.getObject(1).getArray("chunks"), spec)
        val tiles = readTiles(rawTiles)

        return Lands(spec.width.toShort(), spec.height.toShort(), spec.shiftX, spec.shiftY, map, objects, tiles)
    }

    private fun readTiles(rawTiles: JsonObject): Array<Tile?> {
        val tilesColumns = rawTiles.getPrimitive("columns").int
        val tileSize = rawTiles.getPrimitive("tileheight").int
        val count = rawTiles.getPrimitive("tilecount").int
        val tiles = arrayOfNulls<Tile>(count)
        rawTiles.getArray("tiles").forEach { it ->
            val tile = it as JsonObject
            val id = tile.getPrimitive("id").int
            val rawType = tile.getPrimitive("type").content
            val type = parseTileType(rawType)

            tiles[id] = Tile(id, type)
        }

        return tiles
    }

    private fun calcSpec(rawLayers: JsonArray): Spec {

        var isFirst = true
        var maxShiftX = 0
        var maxShiftY = 0
        var minShiftX = 0
        var minShiftY = 0

        val basis = rawLayers.getObject(0).getArray("chunks")

        basis.forEach { it ->

            val chunk = it as JsonObject
            val shiftX = chunk.getPrimitive("x").int
            val shiftY = chunk.getPrimitive("y").int
            if (isFirst) {
                isFirst = false
                minShiftX = shiftX
                minShiftY = shiftY
            }

            if (shiftX > maxShiftX) maxShiftX = shiftX
            if (shiftY > maxShiftY) maxShiftY = shiftY
        }
        val width = maxShiftX - minShiftX + chunkSize
        val height = maxShiftY - minShiftY + chunkSize

        return Spec(width, height, minShiftX, minShiftY)
    }

    private fun readChunks(layers: JsonArray, spec: Spec): ShortArray {
        val map = ShortArray(spec.width * spec.height)


        layers.forEach { it ->

            val chunk = it as JsonObject
            val shiftX = chunk.getPrimitive("x").int
            val shiftY = chunk.getPrimitive("y").int

            val chunkWidth = chunk.getPrimitive("width").int
            val chunkHeight = chunk.getPrimitive("height").int

            //fix me positive
            val posX = shiftX - spec.shiftX
            val posY = shiftY - spec.shiftY
            val data = chunk.getArray("data")

            for (i in 0 until data.size) {

                val v = data.getPrimitive(i).int
                if (v == 0) continue

                val chnukX = i % chunkWidth
                val chnukY = i / chunkHeight
                val coord = posX + chnukX + (posY + chnukY) * spec.width
                map[coord] = (v - 1).toShort() //tile manager increments every tile id (I don't know why)
            }

        }
        return map
    }


    private fun parseTileType(raw: String?): TileType {
        if (raw == null) return NOTHING

        return try {
            TileType.valueOf(raw)
        } catch (e: IllegalArgumentException) {
            NOTHING
        }

    }
}
