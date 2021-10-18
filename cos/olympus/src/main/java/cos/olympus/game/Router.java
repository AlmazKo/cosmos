package cos.olympus.game;

import cos.ops.in.Login;
import cos.ops.in.Logout;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<Integer, Usr> users = new HashMap<>();


    public void onLogin(int tick, Login op) {
        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new Usr(op.userId(), "map");
            users.put(op.userId(), usr);
            System.out.println("New User " + usr);
        }
    }

    public void onLogout(int tick, Logout op) {
//        var cr = world.getCreature(op.userId());
//        if (cr == null) return;
//
        //todo allow finish step
        users.remove(op.userId());
    }

    public String getWorld(int userId) {
        //fixme NPE
        return users.get(userId).worldName;
    }
}
