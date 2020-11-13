package cos.map.parser;

import almazko.microjson.JsArray;
import almazko.microjson.JsObject;
import cos.map.Lands;
import cos.map.Tile;
import cos.map.TileType;
import org.jetbrains.annotations.Nullable;

public class MapParser {

    private final static int chunkSize = 16;

    public static Lands parse(JsObject rawMap, JsObject rawTiles) {
        var layers = rawMap.getArray("layers");
        var spec = calcSpec(layers);
        var map = readChunks(layers.getObject(0).getArray("chunks"), spec);
        var objects = readChunks(layers.getObject(1).getArray("chunks"), spec);
        var tiles = readTiles(rawTiles);

        return new Lands(spec.width, spec.height, spec.shiftX, spec.shiftY, map, objects, tiles);
    }

    private static Tile[] readTiles(JsObject rawTiles) {
        var tilesColumns = rawTiles.getInt("columns");
        var tileSize = rawTiles.getInt("tileheight");
        var count = rawTiles.getInt("tilecount");
        var tiles = new Tile[count];
        rawTiles.getArray("tiles").forEach((it) -> {
            var tile = (JsObject) it;
            var id = tile.getInt("id");
            var rawType = tile.getString("type");
            var type = parseTileType(rawType);

            tiles[id] = new Tile(id, type);
        });
        return tiles;
    }

    private static Spec calcSpec(JsArray rawLayers) {

        var isFirst = true;
        int maxShiftX = 0;
        int maxShiftY = 0;
        int minShiftX = 0;
        int minShiftY = 0;

        var basis = rawLayers.getObject(0).getArray("chunks");

        for (Object it : basis) {
            var chunk = (JsObject) it;
            var shiftX = chunk.getInt("x");
            var shiftY = chunk.getInt("y");
            if (isFirst) {

                isFirst = false;
                minShiftX = shiftX;
                minShiftY = shiftY;
            }

            if (shiftX > maxShiftX) maxShiftX = shiftX;
            if (shiftY > maxShiftY) maxShiftY = shiftY;
        }
        var width = maxShiftX - minShiftX + chunkSize;
        var height = maxShiftY - minShiftY + chunkSize;

        return new Spec(width, height, minShiftX, minShiftY);
    }

    private static short[] readChunks(JsArray layers, Spec spec) {
        var map = new short[spec.width * spec.height];
        for (Object it : layers) {
            var chunk = (JsObject) it;
            var shiftX = chunk.getInt("x");
            var shiftY = chunk.getInt("y");
            var chunkWidth = chunk.getInt("width");
            var chunkHeight = chunk.getInt("height");
            //fix me positive;
            var posX = shiftX - spec.shiftX;
            var posY = shiftY - spec.shiftY;
            var data = chunk.getArray("data");
            for (int i = data.size() - 1; i >= 0; i--) {
                var v = data.getInt(i);
                if (v == 0) continue;
                var chnukX = i % chunkWidth;
                var chnukY = i / chunkHeight;
                var coord = posX + chnukX + (posY + chnukY) * spec.width;
                map[coord] = (short) (v - 1); //tile manager increments every tile id (I don't know why);
            }
        }
        return map;
    }

    private static TileType parseTileType(@Nullable String raw) {

        if (raw == null) return TileType.NOTHING;

        try {
            return TileType.valueOf(raw);
        } catch (IllegalArgumentException e) {
            return TileType.NOTHING;
        }

    }

    static final class Spec {
        int width;
        int height;
        int shiftX;
        int shiftY;

        public Spec(int width, int height, int shiftX, int shiftY) {
            this.width = width;
            this.height = height;
            this.shiftX = shiftX;
            this.shiftY = shiftY;
        }
    }
}
