package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.NoSpaceException;
import cos.ops.OutOp;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static cos.olympus.game.NpcStrategy.rand;

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
                var npc = world.createCreature(new User(id++, "Phantom", 5, 2));
                live = new NpcStrategy(npc, world, movements);
            } catch (NoSpaceException ne) {
                logger.warn("No space");
                return;
            }
        }

        if (live.isDead()) {
            live = null;
            isDead = true;
            respawnTime = rand(10, 60);
            return;
        }

        live.onTick(tick, consumer);
    }
}
