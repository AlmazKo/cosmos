package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.Util;
import cos.ops.Direction;
import cos.ops.in.Move;

public class NpcStrategy {

    private final static Logger logger = Logger.get(NpcStrategy.class);
    private final Creature npc;
    private final World world;
    private final Spells spells;
    private final Movements movements;

    private int nextPlannedTick = -1;

    public NpcStrategy(Creature cr, World world, Spells spells, Movements movements) {
        this.npc = cr;
        this.world = world;
        this.spells = spells;
        this.movements = movements;
    }

    void onTick(int tick) {
        if (tick <= nextPlannedTick) return;

        if (!tryToAttract(tick)) {
            walkingAround();
            nextPlannedTick = tick + Util.rand(10, 20);
        }
    }

    private boolean tryToAttract(int tick) {
        var nextX = MapUtil.nextX(npc);
        var nextY = MapUtil.nextY(npc);
        var near = world.getCreature(nextX, nextY);
        if (near != null) {
            logger.info("" + npc + " aggro-ed " + near);
            spells.onMeleeAttack(tick, npc);
            nextPlannedTick = tick + Util.rand(4, 8);
            return true;
        }

        var nears = world.getCreatures(npc.x, npc.y, 1);
        if (!nears.isEmpty()) {
            var dir = MapUtil.direction(npc, nears.get(0));
            if (dir == null) {
                return false;
            }
            logger.info("" + npc + " attracted " + dir);
            movements.changeSight(npc, dir);
            nextPlannedTick = tick + 1;
            return true;
        }

        return false;
    }

    private void walkingAround() {
        var dir = Direction.values()[Util.rand(0, 4)];
        int x = MapUtil.nextX(npc, dir);
        int y = MapUtil.nextY(npc, dir);

        if (world.isFree(x, y) && world.isNoMovingCreaturesIn(x, y)) {
            var mv = new Move(0, npc.id(), npc.x, npc.y, dir, dir);
            movements.change(npc, mv);
            movements.stop(npc, npc.sight);
        } else {
//                logger.info("Can not move to " + dir + " #" + npc.id);
//                logger.info("Cannot move to " + dir + " #" + npc.id
//                        + ", x=" + x + ", y=" + y + ", free=" + world.isFree(x, y) + ", smth stand=" + world.hasCreature(x, y));
        }
    }

    public boolean isDead() {
        return npc.isDead();
    }
}
