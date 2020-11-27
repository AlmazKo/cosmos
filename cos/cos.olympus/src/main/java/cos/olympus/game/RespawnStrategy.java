package cos.olympus.game;

import cos.logging.Logger;
import cos.map.Coord;
import cos.olympus.NoSpaceException;
import org.jetbrains.annotations.Nullable;


public class RespawnStrategy {

    private final static Logger logger = new Logger(Movements.class);
    private static       int    id     = 10_000;

    private final World     world;
    private final Movements movements;
    private final Coord     spot;

    private @Nullable NpcStrategy live;
    private           boolean     isDead      = false;
    private           int         respawnTime = Integer.MAX_VALUE;

    public RespawnStrategy(World world, Movements movements, Coord spot) {
        this.world = world;
        this.movements = movements;
        this.spot = spot;
    }

    void onTick(int tick) {
        if (live == null) {
            if (isDead && tick < respawnTime) {
                return;
            }
            isDead = false;
            try {
                var npc = world.createCreature(new Npc(++id, "Phantom", spot.x(), spot.y()), 80, 4);
                live = new NpcStrategy(npc, world, movements);
            } catch (NoSpaceException ne) {
                logger.warn("No space");
                return;
            }
        }

        if (live.isDead()) {
            live = null;
            isDead = true;
            respawnTime = tick + Util.rand(100, 300);
            return;
        }

        live.onTick(tick);
    }
}
