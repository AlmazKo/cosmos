package cos.map.parser;

import almazko.microjson.JsArray;
import almazko.microjson.JsObject;
import cos.map.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MapParser {

    private final static int chunkSize = 16;

    public static Lands parse(JsObject rawMap, JsObject rawTiles) {
        var layers = rawMap.getArray("layers");
        var spec = calcSpec(layers);
        short[] map = null;
        short[] objects = null;
        ArrayList<RespawnSpot> respawns = null;
        for (Object l : layers) {
            if (l == null) continue;

            var layer = (JsObject) l;
            switch (layer.getString("name")) {
                case "basic" -> map = readChunks(layer.getArray("chunks"), spec);
                case "objects" -> objects = readChunks(layer.getArray("chunks"), spec);
                case "respawns" -> respawns = readRespawnSpots(layer.getArray("objects"));
            }
        }
        var tiles = readTiles(rawTiles);

        return new Lands(spec.width, spec.height, spec.shiftX, spec.shiftY, map, objects, tiles, respawns);
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

    private static ArrayList<RespawnSpot> readRespawnSpots(JsArray objects) {
        var result = new ArrayList<RespawnSpot>();

        for (Object o : objects) {
            var obj = (JsObject) o;
            var props = obj.getArray("properties");
            int size = findIntProp(props, "size");
            String type = findStringProp(props, "npc_type");
            var spot = new RespawnSpot(obj.getInt("x") / 32, obj.getInt("y") / 32, size, NpcType.valueOf(type.toUpperCase()));
            result.add(spot);
        }
        return result;
    }

    private static int findIntProp(JsArray props, String name) {
        for (Object prop : props) {
            if (name.equals(((JsObject) prop).getString("name"))) {
                return ((JsObject) prop).getInt("value");
            }
        }
        return 0;
    }

    private static String findStringProp(JsArray props, String name) {
        for (Object prop : props) {
            if (name.equals(((JsObject) prop).getString("name"))) {
                return ((JsObject) prop).getString("value");
            }
        }
        return null;
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
