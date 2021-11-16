package cos.olympus.game;

import cos.olympus.Strategy;
import cos.olympus.util.OpsConsumer;
import cos.ops.AnyOp;
import cos.ops.Op;
import cos.ops.in.Login;

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

    public void onTick(int tick, List<AnyOp> in, OpsConsumer out) {
        in.forEach(op -> {
            if (op.code() == Op.LOGIN) {
                strategies.add(new LoginStrategy(games, op.userId()));
            } else {
                games.values().forEach(game -> game.handleIncomeOp(op));
            }
        });


        games.values().forEach(game -> {
            game.onTick(tick, out);
        });


        strategies.removeIf(strategy -> strategy.onTick(tick, out));
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
