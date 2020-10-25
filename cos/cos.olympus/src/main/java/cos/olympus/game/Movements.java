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
    private final        TileMap              map;
    private final        HashMap<Integer, Mv> mvs    = new HashMap<>();

    Movements(TileMap map) {
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
            cr.dir = op.dir();
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

        if (newOffset < 0) {
            cr.offset = newOffset;
        } else if (newOffset < HALF) {
            if (mv.stop) {
                cr.stop();
                logger.info("MV finished " + cr);
                return true;
            }

            cr.offset = newOffset;
        } else {
            int x = nextX(cr);
            int y = nextY(cr);
            var tile = map.get(x, y);
            if (tile == TileType.NOTHING || tile == TileType.DEEP_WATER) {
                mv.rollBack = true;
                cr.speed = -cr.speed;
                //don't touch offset
                logger.info("Rollback " + cr);
            } else {
                cr.x = x;
                cr.y = y;
                cr.offset = newOffset - METER;
                cr.speed = toTickSpeed(getSpeed(tile));
            }
        }

//        if (newOffset < HALF) {
//            cr.offset = newOffset;
//        } else if (newOffset < METER) {
//            if (cr.offset < HALF) {
//                int x = nextX(cr);
//                int y = nextY(cr);
//                var tile = map.get(x, y);
//                if (tile == TileType.NOTHING || tile == TileType.DEEP_WATER) {
//                    mv.rollBack = true;
//                    logger.info("Rollback " + cr);
//                } else {
//                    cr.x = x;
//                    cr.y = y;
//                }
//            }
//            cr.offset = newOffset;
//
//        } else {
//            if (mv.stop) {
//                cr.offset = 0;
//                cr.speed = 0;
//                cr.dir = null;
//                logger.info("MV finished " + cr);
//                return true;
//            } else {
//                cr.offset = newOffset - METER;
//            }
//        }

//
//        if (newOffset < METER) {
//
//            if (mv.stop && cr.offset >= HALF) {
//                cr.offset = HALF;
//                cr.speed = 0;
//                logger.info("MV finished " + cr);
//                return true;
//            }
//
//            cr.offset = newOffset;
//            logger.info("MV " + cr);
//            return false;
//        }
//        if (newOffset >= METER) {
//            int x = nextX(cr);
//            int y = nextY(cr);
//            var tile = map.get(x, y);
////            if (tile == TileType.GRASS) {
//
//            cr.x = x;
//            cr.y = y;
//            cr.offset = newOffset - METER;
//
//            var next = mv.next;
//            if (next != null) {
//                cr.sight = next.sight();
//                cr.dir = next.dir();
//            }
////            } else {
////                cr.speed = tickSpeed(1);
////                cr.offset = 0;
////                cr.dir = cr.dir.opposite();
////                mv.stop = true;
////                mv.rollBack = true;
////                logger.warn(format("%s, rollback tile ... : [%d;%d]", tile, x, y));
////            }
//
//        } else {
//            throw new IllegalStateException("big offset=$newOffset,  $cr");
//        }
//
//        logger.info("MV " + cr);
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
