package cos.olympus.game;

import cos.logging.Logger;
import cos.ops.out.Appear;
import cos.ops.in.Login;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Users {
    private final static Logger logger = new Logger(Users.class);

    private final HashMap<Integer, Player> users = new HashMap<>();
    private final World                    world;

    public Users(World world) {
        this.world = world;
    }

    @Nullable Appear onLogin(int tick, Login op) {
        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new Player(op.userId(), "user:" + op.userId());
            var creature = world.createCreature(usr, 100, 4);
            return new Appear(op.id(), tick, usr.id, creature.x, creature.y, creature.mv, creature.sight, creature.metrics.life());
        } else {
            logger.warn("#" + tick + " " + "User already logged in " + usr);
            return null;
        }
    }

}
