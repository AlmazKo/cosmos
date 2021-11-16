package cos.olympus.game;

import cos.map.CreatureType;
import cos.olympus.NoSpaceException;
import cos.olympus.Util;
import cos.olympus.util.OpConsumer;
import cos.ops.in.Login;
import cos.ops.in.Logout;
import cos.ops.out.Appear;
import cos.ops.out.ProtoAppear;
import org.jetbrains.annotations.Nullable;


public class TeleportStrategy {
    private final World  world;
    private final Player player;
    private final int    respawnTime;

    public TeleportStrategy(int tick, World world, Player player, String worldName) {
        this.world = world;
        this.player = player;
        this.respawnTime = tick + 10;
    }

    boolean onTick(int tick, OpConsumer outOps) {

//        outOps.add(new Logout(1, player.id));
//        outOps.add(new Login(1, player.id));
//        if (tick < respawnTime) return false;
//
//
//
//        try {
//            var cr = world.createCreature(new Npc(player.id, CreatureType.PLAYER, player.name, 34, -24), 100, 1);
//            outOps.add(new Appear(0, tick, cr.id(), cr.x(), cr.y(), cr.mv(), cr.sight(), cr.metrics.lvl, cr.life()));
//        } catch (NoSpaceException e) {
//            return false;
//        }
        return true;
    }



//    @Nullable ProtoAppear onLogin(int tick, Login op) {
//        var usr = users.get(op.userId());
//        if (usr == null) {
//            usr = new Player(op.userId(), "user:" + op.userId());
//            var creature = world.createCreature(usr, 100, 4);
//            return new ProtoAppear(op.id(), tick, usr.id, "map", creature.x, creature.y, creature.sight);
//        } else {
//            logger.warn("#" + tick + " " + "User already logged in " + usr);
//            return null;
//        }
//    }
}
