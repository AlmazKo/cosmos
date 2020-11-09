package cos.olympus.game;

import cos.ops.Direction;

import java.util.concurrent.ThreadLocalRandom;

public class Util {


    public static int nextX(Orientable cr) {
        return Util.nextX(cr, cr.mv());
    }

    public static int nextY(Orientable cr) {
        return Util.nextY(cr, cr.mv());
    }

    public static int nextX(Orientable ort, Direction mv) {
        return switch (mv) {
            case NORTH, SOUTH -> ort.x();
            case WEST -> ort.x() - 1;
            case EAST -> ort.x() + 1;
        };
    }

    public static int nextY(Orientable ort, Direction mv) {
        return switch (mv) {
            case NORTH -> ort.y() - 1;
            case SOUTH -> ort.y() + 1;
            case WEST, EAST -> ort.y();
        };
    }


    public static boolean inZone(Orientable ort, int x, int y, int radius) {
        var oX = ort.x();
        var oY = ort.y();
        return oX <= x + radius && x >= oX - radius && oY <= y + radius && oY >= y - radius;
    }

    public static int rand(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

}
