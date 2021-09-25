package cos.olympus.game;

import cos.logging.Logger;
import cos.map.Coord;
import cos.map.NpcType;
import cos.olympus.NoSpaceException;
import cos.olympus.Util;
import org.jetbrains.annotations.Nullable;


public class RespawnStrategy {

    private final static Logger logger = Logger.get(Movements.class);
    private static       int    id     = 10_000;

    private final World     world;
    private final Spells spells;
    private final Movements movements;
    private final Coord     spot;
    private final NpcType type;

    private @Nullable NpcStrategy live;
    private           boolean     isDead      = false;
    private           int         respawnTime = Integer.MAX_VALUE;

    public RespawnStrategy(World world, Spells spells, Movements movements, Coord spot, NpcType type) {
        this.world = world;
        this.spells = spells;
        this.movements = movements;
        this.spot = spot;
        this.type = type;
    }

    void onTick(int tick) {
        if (live == null) {
            if (isDead && tick < respawnTime) {
                return;
            }
            isDead = false;
            try {
                var npc = world.createCreature(new Npc(++id, type, "Phantom", spot.x(), spot.y()), 80, 4);
                live = new NpcStrategy(npc, world, spells, movements);
            } catch (NoSpaceException ne) {
                logger.warn("No space");
                return;
            }
        }

        if (live.isDead()) {
            live = null;
            isDead = true;
            respawnTime = tick + Util.rand(10, 30);//tmp
            return;
        }

        live.onTick(tick);
    }
}
