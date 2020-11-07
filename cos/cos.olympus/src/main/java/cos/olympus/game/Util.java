package cos.olympus.game;

import cos.ops.Direction;

public class Util {


    public static int nextX(Orientable cr) {
        return Util.nextX(cr, cr.mv());
    }

    public static int nextY(Orientable cr) {
        return Util.nextY(cr, cr.mv());
    }

    public static int nextX(Orientable cr, Direction mv) {
        return switch (mv) {
            case NORTH, SOUTH -> cr.x();
            case WEST -> cr.x() - 1;
            case EAST -> cr.x() + 1;
        };
    }

    public static int nextY(Orientable cr, Direction mv) {
        return switch (mv) {
            case NORTH -> cr.y() - 1;
            case SOUTH -> cr.y() + 1;
            case WEST, EAST -> cr.y();
        };
    }
}
