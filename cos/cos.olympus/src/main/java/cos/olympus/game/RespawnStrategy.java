package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.NoSpaceException;
import cos.ops.OutOp;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


public class RespawnStrategy {

    private final static Logger logger = new Logger(Movements.class);
    private static       int    id     = 10_000;

    private final World        world;
    private final Movements    movements;
    private final CreatureType type;


    private @Nullable NpcStrategy live;

    private boolean isDead      = false;
    private int     respawnTime = Integer.MAX_VALUE;

    public RespawnStrategy(World world, Movements movements, CreatureType type) {
        this.world = world;
        this.movements = movements;
        this.type = type;
    }

    void onTick(int tick, Collection<OutOp> consumer) {
        if (live == null) {
            if (isDead && tick < respawnTime) {
                return;
            }
            isDead = false;
            try {
                var npc = world.createCreature(new Npc(++id, "Phantom", 7, 3), 80, 6);
                live = new NpcStrategy(npc, world, movements);
            } catch (NoSpaceException ne) {
                logger.warn("No space");
                return;
            }
        }

        if (live.isDead()) {
            live = null;
            isDead = true;
            respawnTime = tick + Util.rand(600, 3000);
            return;
        }

        live.onTick(tick, consumer);
    }
}
