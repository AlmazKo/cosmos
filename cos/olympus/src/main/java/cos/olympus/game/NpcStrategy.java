package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.Util;
import cos.ops.Direction;
import cos.ops.in.Move;
import org.jetbrains.annotations.Nullable;

public class NpcStrategy implements TickAware {

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

    @Override
    public void onTick(int tick) {
        if (tick <= nextPlannedTick) return;

        if (!tryToAttract(tick)) {
            walkingAround();
            nextPlannedTick = tick + Util.rand(10, 20);
        }
    }

    private boolean tryToAttract(int tick) {
        if (!npc.avatar.type().isAggressive()) return false;

        var nextX = MapUtil.nextX(npc);
        var nextY = MapUtil.nextY(npc);
        var near = world.getCreature(nextX, nextY);
        if (near != null && near.type() != npc.type()) {
            logger.info("" + npc + " aggro-ed " + near);
            spells.onMeleeAttack(tick, npc);
            nextPlannedTick = tick + Util.rand(4, 8);
            return true;
        }

        var dir = turnTo();
        if (dir == null) return false;

        logger.info("" + npc + " attracted " + dir);
        movements.changeSight(npc, dir);
        nextPlannedTick = tick + 1;
        return true;
    }

    private @Nullable Direction turnTo() {
        var nears = world.getCreatures(npc.x, npc.y, 1);

        for (Creature near : nears) {
            if (near.type() != npc.type()) {
                var dir = MapUtil.direction(npc, near);
                if (dir != null) return dir;
            }
        }

        return null;
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
