package cos.olympus.game;

import cos.logging.Logger;
import cos.map.TileType;
import cos.olympus.ops.Move;

import java.util.HashMap;

import static java.lang.String.format;

final class Movements {


    public final static  int                  HALF   = 16;
    private final static int                  YARD   = 32;
    private final static Logger               logger = new Logger(Movements.class);
    private final        GameMap              map;
    private final        HashMap<Integer, Mv> mvs    = new HashMap<>();

    Movements(GameMap map) {
        this.map = map;
    }

    class Mv {
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
        }

        var currentTile = map.get(cr.x, cr.y);

        cr.speed = switch (currentTile) {
            case GRASS -> 5;
            case SHALLOW -> 2;
            default -> throw new IllegalStateException();
        };

    }


    void update() {
        //todo remove allocations
        mvs.values().removeIf(this::onTick);
        //            int dir = c << 1;
        //            int offset = c << 2;
        //            int newOffset = (offset + speed) % 16;
    }

    private boolean onTick(Mv mv) {
        var cr = mv.cr;
        var newOffset = cr.offset + cr.speed;

        if (newOffset < YARD) {

            if (mv.stop && cr.offset >= HALF) {
                cr.offset = HALF;
                cr.speed = 0;
                logger.info("MV finished " + cr);
                return true;
            }

            cr.offset = newOffset;
            logger.info("MV " + cr);
            return false;
        }

        if (newOffset >= YARD) {
            int x = nextX(cr);
            int y = nextY(cr);
            var tile = map.get(x, y);
            if (tile == TileType.GRASS) {

                cr.x = x;
                cr.y = y;
                cr.offset = newOffset - YARD;

                var next = mv.next;
                if (next != null) {
//                    cr.sight = next.sight;
//                    cr.dir = next.dir;
                }
            } else {
                cr.speed = 1;
                cr.offset = 0;
                cr.dir = cr.dir.opposite();
                mv.stop = true;
                mv.rollBack = true;
                logger.warn(format("%s, rollback tile ... : [%d;%d]", tile, x, y));
            }

        } else {
            throw new IllegalStateException("big offset=$newOffset,  $cr");
        }

        logger.info("MV " + cr);
        return false;
    }


    public static int nextX(Creature cr) {
        return switch (cr.dir) {
            case NORTH, SOUTH -> cr.x;
            case WEST -> cr.x - 1;
            case EAST -> cr.x + 1;
        };
    }

    public static int nextY(Creature cr) {
        return switch (cr.dir) {
            case NORTH -> cr.y - 1;
            case SOUTH -> cr.y + 1;
            case WEST, EAST -> cr.y;
        };
    }
}
