package cos.olympus.game.strategy;

import cos.map.CreatureType;
import cos.olympus.NoSpaceException;
import cos.olympus.Util;
import cos.olympus.game.Npc;
import cos.olympus.game.Player;
import cos.olympus.game.World;
import cos.olympus.util.OpConsumer;
import cos.ops.out.Appear;


public class RespawnPlayerStrategy implements Strategy {
    private final World world;
    private final Player player;
    private final int respawnTime;

    public RespawnPlayerStrategy(int tick, World world, Player player) {
        this.world = world;
        this.player = player;
        this.respawnTime = tick + Util.rand(20, 40);
    }

    @Override
    public boolean onTick(int tick, OpConsumer outOps) {
        if (tick < respawnTime) return false;
        try {
            var cr = world.place(new Npc(player.id(), CreatureType.PLAYER, player.name()), 34, -24, 100, 1);
            outOps.add(new Appear(0, tick, cr.id(), cr.x(), cr.y(), cr.mv(), cr.sight(), cr.metrics().lvl, cr.life()));
        } catch (NoSpaceException e) {
            return false;
        }
        return true;
    }
}
