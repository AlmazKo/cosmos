package cos.olympus.game;

import cos.ops.Direction;

public class Util {




    public static int nextX(Creature cr, Direction mv) {
        return switch (mv) {
            case NORTH, SOUTH -> cr.x;
            case WEST -> cr.x - 1;
            case EAST -> cr.x + 1;
        };
    }

    public static int nextY(Creature cr, Direction mv) {
        return switch (mv) {
            case NORTH -> cr.y - 1;
            case SOUTH -> cr.y + 1;
            case WEST, EAST -> cr.y;
        };
    }
}
