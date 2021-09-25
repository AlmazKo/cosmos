package cos.olympus.game;

import cos.ops.Direction;
import org.jetbrains.annotations.Nullable;

import static cos.ops.Direction.*;

public final class MapUtil {

    public static int nextX(Orientable cr) {
        var dir = cr.mv() == null ? cr.sight() : cr.mv();
        return MapUtil.nextX(cr, dir);
    }

    public static int nextY(Orientable cr) {
        var dir = cr.mv() == null ? cr.sight() : cr.mv();
        return MapUtil.nextY(cr, dir);
    }

    public static int nextX(Placeable ort, Direction mv) {
        return switch (mv) {
            case NORTH, SOUTH -> ort.x();
            case WEST -> ort.x() - 1;
            case EAST -> ort.x() + 1;
        };
    }

    public static int nextY(Placeable ort, Direction mv) {
        return switch (mv) {
            case NORTH -> ort.y() - 1;
            case SOUTH -> ort.y() + 1;
            case WEST, EAST -> ort.y();
        };
    }


    public static boolean inZone(Placeable ort, int x, int y, int radius) {
        var oX = ort.x();
        var oY = ort.y();
        return oX <= x + radius && x >= oX - radius && oY <= y + radius && oY >= y - radius;
    }

    public static @Nullable Direction direction(Placeable from, Placeable to) {
        if (from.y() == to.y()) {
            if (from.x() < to.x()) {
                return EAST;
            } else {
                return WEST;
            }
        } else if (from.x() == to.x()) {
            if (from.y() < to.y()) {
                return SOUTH;
            } else {
                return NORTH;
            }
        } else {
            return null;
        }
    }
}
