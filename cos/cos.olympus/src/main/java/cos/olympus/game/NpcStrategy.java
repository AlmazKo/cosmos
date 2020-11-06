package cos.olympus.game;

import cos.logging.Logger;
import cos.ops.Direction;
import cos.ops.Move;
import cos.ops.OutOp;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class NpcStrategy {

    private final static Logger    logger          = new Logger(Movements.class);
    private final        Creature  npc;
    private final        World     world;
    private final        Movements movements;
    private              int       nextPlannedTick = -1;

    public NpcStrategy(Creature cr, World world, Movements movements) {
        this.npc = cr;
        this.world = world;
        this.movements = movements;
    }

    void onTick(int tick, Collection<OutOp> consumer) {
        if (tick > nextPlannedTick) {
            var dir = Direction.values()[rand(0, 4)];
            if (world.isFree(Util.nextX(npc, dir), Util.nextY(npc, dir))) {
                var mv = new Move(0, npc.id, npc.x, npc.y, dir, dir);
                movements.start(npc, mv);
                movements.stop(npc);
            } else {
                logger.info("Can not move to " + dir + " #" + npc.id);
            }
            nextPlannedTick = tick + rand(10, 20);
        }
    }

    public static int rand(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    public boolean isDead() {
        return npc.isDead();
    }
}
