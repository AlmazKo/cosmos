package cos.olympus.game.strategy;

import cos.logging.Logger;
import cos.map.Coord;
import cos.map.CreatureType;
import cos.olympus.NoSpaceException;
import cos.olympus.Util;
import cos.olympus.game.Movements;
import cos.olympus.game.Npc;
import cos.olympus.game.Spells;
import cos.olympus.game.World;
import cos.olympus.util.OpConsumer;
import org.jetbrains.annotations.Nullable;


public class RespawnStrategy implements Strategy {

    private final static Logger logger = Logger.get(Movements.class);
    private static int id = 10_000;

    private final World world;
    private final Spells spells;
    private final Movements movements;
    private final Coord spot;
    private final CreatureType type;

    private @Nullable NpcStrategy live;
    private boolean isDead = false;
    private int respawnTime = Integer.MAX_VALUE;

    public RespawnStrategy(World world, Spells spells, Movements movements, Coord spot, CreatureType type) {
        this.world = world;
        this.spells = spells;
        this.movements = movements;
        this.spot = spot;
        this.type = type;
    }


    @Override
    public boolean onTick(int tick, OpConsumer out) {
        if (live == null) {
            if (isDead && tick < respawnTime) {
                return false;
            }
            isDead = false;
            try {
                var npc = world.place(new Npc(++id, type, "Phantom"), spot.x(), spot.y(), 80, 4);
                live = new NpcStrategy(npc, world, spells, movements);
            } catch (NoSpaceException ne) {
                logger.warn("No space");
                return false;
            }
        }

        if (live.isDead()) {
            live = null;
            isDead = true;
            respawnTime = tick + Util.rand(10, 30);//tmp
            return false;
        }

        live.onTick(tick, out);
        return false;
    }
}
