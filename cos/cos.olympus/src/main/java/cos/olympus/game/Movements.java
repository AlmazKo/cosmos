package cos.olympus.game;

import cos.logging.Logger;
import cos.map.TileType;
import cos.ops.Move;

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
            case SHALLOW, GATE -> 100;
            default -> throw new IllegalStateException("Unsupported " + currentTile);
        };
    }


    void interrupt(Creature cr) {
        mvs.remove(cr.id);
    }

    void stop(Creature cr) {
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

        if (cannotStep(cr, x, y)) {
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

            if (mv.next != null) {
                cr.mv = mv.next.dir();
                cr.sight = mv.next.sight();
                mv.next = null;
            }

            cr.offset = newOffset - METER;
            var tile = map.get(x, y);
            cr.speed = toTickSpeed(getSpeed(tile));
            logger.info("MV " + cr);
            return false;
        }
    }

    private boolean cannotStep(Creature cr, int x, int y) {
        var obj = map.getObject(x, y);
        if (obj != null && obj.tile().type() == TileType.WALL) {
            return true;
        }
        var tile = map.get(x, y);
        return tile == TileType.NOTHING || tile == TileType.DEEP_WATER || tile == TileType.WALL;
    }


    static int nextX(Creature cr) {
        return Util.nextX(cr, cr.mv);
    }

    static int nextY(Creature cr) {
        return Util.nextY(cr, cr.mv);
    }

}
