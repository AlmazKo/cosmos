package cos.olympus.game;

import cos.map.NpcType;
import cos.olympus.NoSpaceException;
import cos.olympus.Util;
import cos.olympus.util.OpConsumer;
import cos.ops.out.Appear;


public class RespawnPlayerStrategy {
    private final World  world;
    private final Player player;
    private final int    respawnTime;

    public RespawnPlayerStrategy(int tick, World world, Player player) {
        this.world = world;
        this.player = player;
        this.respawnTime = tick + Util.rand(20, 40);
    }

    boolean onTick(int tick, OpConsumer outOps) {
        if (tick < respawnTime) return false;
        try {
            var cr = world.createCreature(new Npc(player.id, NpcType.PLAYER, player.name, 34, -24), 100, 1);
            outOps.add(new Appear(0, tick, cr.id(), cr.x(), cr.y(), cr.mv(), cr.sight(), cr.metrics.lvl, cr.life()));
        } catch (NoSpaceException e) {
            return false;
        }
        return true;
    }
}
