package cos.api;

import cos.logging.Logger;
import cos.ops.Direction;
import cos.ops.OutOp;
import cos.ops.UserOp;
import cos.ops.in.FireballEmmit;
import cos.ops.in.Login;
import cos.ops.in.Logout;
import cos.ops.in.MeleeAttack;
import cos.ops.in.Move;
import cos.ops.in.ShotEmmit;
import cos.ops.in.StopMove;
import cos.ops.out.UserPackage;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class PlayerSession {
    private final AtomicInteger cid = new AtomicInteger(0);
    private final Logger log = Logger.get(getClass());
    private final Consumer<UserOp> olympus;
    private final ServerWebSocket ws;
    private final int userId;
    private volatile boolean isClosed = false;


    boolean isClosed() {
        return isClosed;
    }

    PlayerSession(ServerWebSocket ws,
                  int userId,
                  Consumer<UserOp> olympus) {

        this.ws = ws;
        this.userId = userId;
        this.olympus = olympus;
        log.info("Connected player: #" + userId);
        setupClient();

        ws.closeHandler(it -> {
            isClosed = true;
            send(new Logout(cid(), userId));
            log.info("Client socket is closing ... ");
        });

        ws.textMessageHandler(this::onRequest);
    }

    private void send(UserOp op) {
        olympus.accept(op);
    }

    private void onRequest(String msg) {
        var js = new JsonObject(msg);
        var request = parseRequest(js);
        if (request != null) send(request);
    }

    private @Nullable UserOp parseRequest(JsonObject js) {
        return switch (js.getString("op")) {
            case "move" -> {
                var dir = asDir(js.getString("dir"));
                var sight = asDir(js.getString("sight"));
                if (sight == null) sight = dir;
                if (dir == null) throw new IllegalArgumentException("Wrong move request");
                yield new Move(cid(), userId, js.getInteger("x"), js.getInteger("y"), dir, sight);
            }
            case "emmit_fireball" -> new FireballEmmit(cid(), userId);
            case "emmit_shot" -> new ShotEmmit(cid(), userId);
            case "melee_attack" -> new MeleeAttack(cid(), userId);
            case "stop_move" ->
                    new StopMove(cid(), userId, js.getInteger("x"), js.getInteger("y"), asDir(js.getString("sight")));
            default -> null;
        };

    }

    private int cid() {
        return cid.incrementAndGet();
    }

    @Nullable Direction asDir(@Nullable String raw) {
        if (raw == null) return null;
        return Direction.valueOf(raw);
    }

    void onOp(UserPackage pkg) {
        var messages = new JsonArray();

        for (Record op : pkg.ops()) {
            messages.add(JsonMapper.toJson((OutOp) op));
        }

        var clientRes = new JsonObject()
                .put("tick", pkg.tick()) //todo hardcode
                .put("time", System.currentTimeMillis() / 1000)
                .put("messages", messages);
        ws.writeTextMessage(clientRes.toString());
    }

    private void setupClient() {
        send(new Login(cid(), userId));
    }
}
