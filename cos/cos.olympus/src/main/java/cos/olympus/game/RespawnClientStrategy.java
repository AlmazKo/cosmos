package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.NoSpaceException;
import cos.ops.Appear;
import cos.ops.OutOp;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


public class RespawnClientStrategy {

    private final static Logger logger = new Logger(Movements.class);

    private final World  world;
    private final Player player;


    private @Nullable NpcStrategy live;

    private boolean isDead      = false;
    private int     respawnTime = Integer.MAX_VALUE;

    public RespawnClientStrategy(World world, Player player) {
        this.world = world;
        this.player = player;
    }

    boolean onTick(int tick, Collection<OutOp> outOps) {
        try {
            var cr = world.createCreature(new Npc(player.id, player.name, 34, -24), 100, 1);
            outOps.add(new Appear(0, tick, cr.id(), cr.x(), cr.y(), cr.mv(), cr.sight(), cr.life));
        } catch (NoSpaceException e) {
            return false;
        }
        return true;
    }
}
