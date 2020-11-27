package cos.olympus.game;

import cos.logging.Logger;
import cos.ops.Direction;
import cos.ops.Move;

public class NpcStrategy {

    private final static Logger    logger          = new Logger(NpcStrategy.class);
    private final        Creature  npc;
    private final        World     world;
    private final        Movements movements;
    private              int       nextPlannedTick = -1;

    public NpcStrategy(Creature cr, World world, Movements movements) {
        this.npc = cr;
        this.world = world;
        this.movements = movements;
    }

    void onTick(int tick) {
        if (tick > nextPlannedTick) {
            var dir = Direction.values()[Util.rand(0, 4)];
            int x = Util.nextX(npc, dir);
            int y = Util.nextY(npc, dir);

            if (world.isFree(x, y) && world.isNoMovingCreaturesIn(x, y)) {
                var mv = new Move(0, npc.id(), npc.x, npc.y, dir, dir);
                movements.change(npc, mv);
                movements.stop(npc, npc.sight);
            } else {
//                logger.info("Can not move to " + dir + " #" + npc.id);
//                logger.info("Cannot move to " + dir + " #" + npc.id
//                        + ", x=" + x + ", y=" + y + ", free=" + world.isFree(x, y) + ", smth stand=" + world.hasCreature(x, y));
            }
            nextPlannedTick = tick + Util.rand(10, 20);
        }
    }

    public boolean isDead() {
        return npc.isDead();
    }
}
