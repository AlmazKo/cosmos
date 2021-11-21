package cos.api;

import cos.logging.Logger;
import cos.ops.out.AllCreatures;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class AdminSession {
    private final AtomicInteger cid = new AtomicInteger(0);
    private final Logger log = Logger.get(getClass());
    private final ServerWebSocket ws;
    private volatile boolean isClosed = false;


    boolean isClosed() {
        return isClosed;
    }

    AdminSession(ServerWebSocket ws) {

        this.ws = ws;
        log.info("Connected admin");

        ws.closeHandler(it -> {
            isClosed = true;
            log.info("Admin socket is closing ... ");
        });

        ws.textMessageHandler(this::onRequest);
    }

    private void onRequest(String msg) {

    }

    void onOp(Record op) {
        if (op instanceof AllCreatures aop) {
            ws.writeTextMessage(toJson(aop).toString());
        } else {
            ws.writeTextMessage("{}");
        }
    }

    private static JsonObject toJson(AllCreatures op) {
        var arr = new ArrayList<Integer>(op.creatures().length);

        for (int i : op.creatures()) {
            arr.add(i);
        }

        return new JsonObject()
                .put("action", "all-creatures")
                .put("width", op.width())
                .put("height", op.height())
                .put("offsetX", op.offsetX())
                .put("offsetY", op.offsetY())
                .put("data", new JsonArray(arr));
    }
}
