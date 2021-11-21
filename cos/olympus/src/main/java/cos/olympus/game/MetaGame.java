package cos.olympus.game;

import cos.olympus.Strategy;
import cos.olympus.util.OpsConsumer;
import cos.ops.Op;
import cos.ops.UserOp;
import cos.ops.in.Login;
import cos.ops.out.AllCreatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaGame {
    private final Map<Integer, Usr> users = new HashMap<>();
    private final Map<String, Game> games;
    private final List<Strategy> strategies = new ArrayList<>();

    public MetaGame(Map<String, Game> games) {
        this.games = games;
    }

    public void onTick(int tick, List<UserOp> in, OpsConsumer out) {
        in.forEach(op -> {
            if (op.code() == Op.LOGIN) {
                strategies.add(new LoginStrategy(games, op.userId()));
            } else {
                games.values().forEach(game -> game.handleIncomeOp(op));
            }
        });

//        var metrics =

        games.values().forEach(game -> {
            game.onTick(tick, out);
            collectMetrics(out, game);
        });


        strategies.removeIf(strategy -> strategy.onTick(tick, out));
    }

    private void collectMetrics(OpsConsumer out, Game game) {
        var crs = game.getWorld().getAllCreatures();
        if (crs.isEmpty()) return;

        int i = 0;
        var data = new int[crs.size() * 3];
        for (Creature cr : crs) {
            data[i++] = cr.x;
            data[i++] = cr.y;
            data[i++] = cr.type().ordinal();
        }
        var w = game.getWorld();
        var op = new AllCreatures(w.width,w.height, w.offsetX, w.offsetY,data);
        out.add(op);
    }

    public void onLogin(int tick, Login op) {
        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new Usr(op.userId(), "map");
            users.put(op.userId(), usr);
            System.out.println("New User " + usr);
        }
    }

}
