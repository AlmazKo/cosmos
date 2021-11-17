package cos.api;

import cos.logging.Logger;
import cos.ops.AnyOp;
import cos.ops.Direction;
import cos.ops.OutOp;
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
import java.util.function.Function;

class PlayerSession {
    private final    AtomicInteger            cid      = new AtomicInteger(0);
    private final    Logger                   log      = Logger.get(getClass());
    private final    Function<AnyOp, Integer> olympus;
    private final    ServerWebSocket          ws;
    private final    int                      userId;
    private volatile boolean                  isClosed = false;


    boolean isClosed() {
        return isClosed;
    }

    PlayerSession(ServerWebSocket ws,
                  int userId,
                  Function<AnyOp, Integer> olympus) {

        this.ws = ws;
        this.userId = userId;
        this.olympus = olympus;
        log.info("Connected player: #" +userId);
        setupClient();

        ws.closeHandler(it -> {
            isClosed = true;
            send(new Logout(cid.incrementAndGet(), userId));
            log.info("Client socket is closing ... ");
        });

        ws.textMessageHandler(this::onRequest);
    }

    private void send(AnyOp op) {
        olympus.apply(op);
    }

    private void onRequest(String msg) {
        var js = new JsonObject(msg);
        var op = parseRequest(js);
        if (op == null) return;

        send(op);
    }

    private @Nullable AnyOp parseRequest(JsonObject js) {
        return switch (js.getString("op")) {
            case "move" -> {
                @Nullable String dirId = js.getString("dir");
                var sightId = js.getString("sight");
                if (sightId == null) sightId = dirId;
                if (dirId == null && sightId == null) throw new IllegalArgumentException("Wrong move request");

                yield new Move(
                        cid.incrementAndGet(),
                        userId,
                        js.getInteger("x"),
                        js.getInteger("y"),
                        (dirId == null) ? null : Direction.valueOf(dirId),
                        Direction.valueOf(sightId)
                );
            }
            case "emmit_fireball" -> new FireballEmmit(
                    cid.incrementAndGet(),
                    userId
            );
            case "emmit_shot" -> new ShotEmmit(
                    cid.incrementAndGet(),
                    userId
            );

            case "melee_attack" -> new MeleeAttack(cid.incrementAndGet(), userId);
            case "stop_move" -> new StopMove(
                    cid.incrementAndGet(),
                    userId,
                    js.getInteger("x"),
                    js.getInteger("y"),
                    Direction.valueOf(js.getString("sight"))
            );
            default -> null;
        };

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
        send(new Login(cid.incrementAndGet(), userId));
    }
}
