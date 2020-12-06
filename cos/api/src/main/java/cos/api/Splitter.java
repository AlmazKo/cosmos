package cos.api;

import cos.map.Lands;
import cos.map.Tile;
import kotlin.Pair;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.floorDiv;
import static java.lang.Math.floorMod;

public class Splitter {

    public static Map<Pair<Integer, Integer>, Tile[]> split16(Lands lands) {
        var result = new HashMap<Pair<Integer, Integer>, Tile[]>();

        Tile t;
        int width = lands.width();
        int height = lands.height();
        int x, y, idx;
        var basis = lands.basis();
        var tiles = lands.tiles();

        for (int i = 0; i < basis.length; i++) {

            t = tiles[basis[i]];
            if (t == null) {
                continue;
            }

            x = i % width + lands.offsetX();
            y = i / width + lands.offsetY();

            var xx = floorDiv(x, 16);
            var yy = floorDiv(y, 16);
//            idx = toIndex(xx, yy);
            var chunk = result.computeIfAbsent(new Pair<>(xx, yy), p -> new Tile[16 * 16]);

            var xi = floorMod(x, 16);
            var yi = floorMod(y, 16);
            chunk[yi * 16 + xi % 16] = t;
        }

        return result;
    }
    public static Map<Pair<Integer, Integer>, Tile[]> splitObjects16(Lands lands) {
        var result = new HashMap<Pair<Integer, Integer>, Tile[]>();

        Tile t;
        int width = lands.width();
        int height = lands.height();
        int x, y, idx;
        var objects = lands.objects();
        var tiles = lands.tiles();

        for (int i = 0; i < objects.length; i++) {

            t = tiles[objects[i]];
            if (t == null) {
                continue;
            }

            x = i % width + lands.offsetX();
            y = i / width + lands.offsetY();

            var xx = floorDiv(x, 16);
            var yy = floorDiv(y, 16);
//            idx = toIndex(xx, yy);
            var chunk = result.computeIfAbsent(new Pair<>(xx, yy), p -> new Tile[16 * 16]);

            var xi = floorMod(x, 16);
            var yi = floorMod(y, 16);
            chunk[yi * 16 + xi % 16] = t;
        }

        return result;
    }

    public static int toIndex(int xx, int yy) {
        return xx << 8 + yy;
    }

    public static String fromIndex(int i) {
        return "" + (i >> 8) + "x";
    }

    private static int toInternalIdx(int v, int i) {
        if (v >= 0) {
            return v % i;
        } else {

            //fixme: optimeze
            var r = v % i;
            if (r == 0) {
                return 0;
            } else {
                return i + r;
            }

        }
    }


}
