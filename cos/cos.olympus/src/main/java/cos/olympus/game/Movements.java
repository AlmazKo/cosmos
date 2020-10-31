package cos.olympus.game;

import cos.logging.Logger;
import cos.map.TileType;
import cos.ops.Move;
import cos.ops.StopMove;

import java.util.HashMap;

import static cos.olympus.Main.toTickSpeed;

final class Movements implements TickAware {

    public final static  int                  HALF   = 50;
    public final static  int                  METER  = 100;
    private final static Logger               logger = new Logger(Movements.class);
    private final        GMap                 map;
    private final        HashMap<Integer, Mv> mvs    = new HashMap<>();

    Movements(GMap map) {
        this.map = map;
    }

    final static class Mv {
        final Creature cr;
        Move    next;
        boolean stop     = false;
        boolean rollBack = false;

        Mv(Creature cr) {
            this.cr = cr;
        }
    }

    void start(Creature cr, Move op) {
        var mv = mvs.get(cr.id);
        if (mv != null) {
            mv.next = op;
        } else {
            mvs.put(cr.id, new Mv(cr));
            cr.mv = op.dir();
            cr.sight = op.sight();
        }

        var currentTile = map.get(cr.x, cr.y);
        cr.speed = toTickSpeed(getSpeed(currentTile));
    }

    private static int getSpeed(TileType currentTile) {
        return switch (currentTile) {
            case GRASS -> 400;
            case SHALLOW -> 200;
            default -> throw new IllegalStateException();
        };
    }


    void stop(Creature cr, StopMove op) {
        var mv = mvs.get(cr.id);
        if (mv != null) {
            mv.stop = true;
        }
    }


    public void onTick(int tickId, long time) {
        //todo remove allocations
        mvs.values().removeIf(this::onTick);
    }

    private boolean onTick(Mv mv) {
        var cr = mv.cr;
        var newOffset = cr.offset + cr.speed;

        if (mv.rollBack) {
            if (newOffset > 0) {
                cr.offset = newOffset;
                return false;
            } else {
                cr.stop();
                return true;
            }
        }

        if (newOffset < METER) {
            cr.offset = newOffset;
            return false;
        }

        int x = nextX(cr);
        int y = nextY(cr);
        var tile = map.get(x, y);

        if (cannotStep(cr, tile)) {
            mv.rollBack = true;
            cr.speed = -cr.speed;
            //don't touch offset
            logger.info("Rollback " + cr);
            return false;
        }

        map.moveCreature(cr, x, y);

        if (mv.stop) {
            cr.stop();
            logger.info("MV finished " + cr);
            return true;
        } else {
            cr.offset = newOffset - METER;
            cr.speed = toTickSpeed(getSpeed(tile));
            logger.info("MV " + cr);
            return false;
        }
    }

    private boolean cannotStep(Creature cr, TileType tile) {
        return tile == TileType.NOTHING || tile == TileType.DEEP_WATER;
    }


    public static int nextX(Creature cr) {
        return switch (cr.mv) {
            case NORTH, SOUTH -> cr.x;
            case WEST -> cr.x - 1;
            case EAST -> cr.x + 1;
        };
    }

    public static int nextY(Creature cr) {
        return switch (cr.mv) {
            case NORTH -> cr.y - 1;
            case SOUTH -> cr.y + 1;
            case WEST, EAST -> cr.y;
        };
    }
}
