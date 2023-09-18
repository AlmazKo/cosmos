package cos.olympus.game;

import cos.logging.Logger;
import cos.map.TileType;
import cos.ops.Direction;
import cos.ops.in.Move;
import cos.ops.in.StopMove;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static cos.olympus.game.MapUtil.nextX;
import static cos.olympus.game.MapUtil.nextY;
import static cos.olympus.util.TimeUtil.toTickSpeed;

public final class Movements implements TickAware {

    public final static int HALF = 50;
    public final static int METER = 100;
    private final static Logger logger = Logger.get(Movements.class);
    private final World world;
    private final HashMap<Integer, Mv> mvs = new HashMap<>();

    Movements(World world) {
        this.world = world;
    }

    public void onMove(Move op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;

        change(cr, op);
    }

    public void onStopMove(StopMove op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;

        stop(cr, op.sight());
    }

    private final static class Mv {
        final Creature cr;
        Move next;
        boolean stop = false;

        Mv(Creature cr) {
            this.cr = cr;
        }
    }

    public void changeSight(Creature cr, Direction sight) {
        var mv = mvs.get(cr.id());
        if (mv != null) {
            //fixme this is simulation
            mv.next = new Move(-1, -1, cr.x, cr.y, null, sight);
        } else {
            cr.setSight(sight);
        }
    }

    public void change(Creature cr, Move op) {
        var mv = mvs.get(cr.id());
        if (mv != null) {
            mv.next = op;
        } else {
            if (op.dir() == null) {
                cr.setSight(op.sight());
                return;
            }

            mvs.put(cr.id(), new Mv(cr));
            cr.mv = op.dir();
            cr.setSight(op.sight());
        }

        var currentTile = world.get(cr.x, cr.y);
        cr.speed = toTickSpeed(getSpeed(currentTile));
    }

    private static int getSpeed(@Nullable TileType currentTile) {
        if (currentTile == null) throw new IllegalStateException("Null title");

        return switch (currentTile) {
            case GRASS, SAND, TIMBER -> 400;
            case SHALLOW, GATE -> 100;
            default -> throw new IllegalStateException("Unsupported " + currentTile);
        };
    }


    void interrupt(Creature cr) {
        mvs.remove(cr.id());
    }

    public void stop(Creature cr, Direction sight) {
        var mv = mvs.get(cr.id());
        if (mv != null) {
            mv.stop = true;
        } else {
            cr.setSight(sight);
        }
    }


    public void onTick(int tickId) {
        mvs.values().removeIf(this::onTick);
    }

    private boolean onTick(Mv mv) {
        var cr = mv.cr;
        var newOffset = cr.offset + cr.speed;
        if (newOffset < METER) {
            cr.offset = newOffset;
            return false;
        }

        int x = nextX(cr);
        int y = nextY(cr);

        if (cannotStep(cr, x, y) || world.hasCreature(x, y)) {
            cr.offset = 0;
            logger.info("Reset " + cr);

            if (mv.stop) {
                cr.stop();
                logger.info("MV finished " + cr);
                return true;
            }
            return false;
        }

        world.moveCreature(cr, x, y);

        if (mv.stop) {
            cr.stop();
            logger.info("MV finished " + cr);
            return true;
        } else {

            if (mv.next != null) {
                cr.mv = mv.next.dir();
                cr.setSight(mv.next.sight());
                mv.next = null;
            }

            cr.offset = newOffset - METER;
            var tile = world.get(x, y);
            cr.speed = toTickSpeed(getSpeed(tile));
            logger.info("MV " + cr);
            return false;
        }
    }

    private boolean cannotStep(Creature cr, int x, int y) {
        var obj = world.getObject(x, y);
        if (obj != null && obj.tile().type() == TileType.WALL) {
            return true;
        }
        var tile = world.get(x, y);
        return tile == TileType.NOTHING || tile == TileType.DEEP_WATER || tile == TileType.WALL;
    }
}
