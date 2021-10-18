package cos.olympus.game;

import cos.logging.Logger;
import cos.ops.in.Login;
import cos.ops.out.ProtoAppear;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class WorldUsers {
    private final static Logger logger = Logger.get(WorldUsers.class);

    private final HashMap<Integer, Player> users = new HashMap<>();
    private final World world;

    public WorldUsers(World world) {
        this.world = world;
    }

    @Nullable ProtoAppear onLogin(int tick, Login op) {
        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new Player(op.userId(), "user:" + op.userId());
            var creature = world.createCreature(usr, 100, 4);
            return new ProtoAppear(op.id(), tick, usr.id, "map", creature.x, creature.y, creature.sight);
        } else {
            logger.warn("#" + tick + " " + "User already logged in " + usr);
            return null;
        }
    }

}
