package cos.api;

import cos.logging.Logger;
import cos.map.Lands;
import cos.ops.out.AllCreatures;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class AdminSession {
    private final AtomicInteger cid = new AtomicInteger(0);
    private final Logger log = Logger.get(getClass());
    private final Map<String, Lands> lands;
    private final ServerWebSocket ws;


    AdminSession(Map<String, Lands> lands, ServerWebSocket ws) {
        this.lands = lands;

        this.ws = ws;
        log.info("Connected admin");

        ws.closeHandler(it -> {
            log.info("Admin socket is closed");
        });

        ws.textMessageHandler(this::onRequest);
        ws.writeTextMessage(toJson(lands.get("map")).toString());
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

    private static JsonObject toJson(Lands op) {
        var result = new ArrayList<Integer>(op.basis().length);
        short[] basis = op.basis();
        for (int tileId : basis) {
            var tile = op.tiles()[tileId];
            if (tile == null) {
                result.add(0);
            } else {
                result.add((int) tile.type().getId());
            }
        }

        return new JsonObject()
                .put("action", "world")
                .put("width", op.width())
                .put("height", op.height())
                .put("offsetX", op.offsetX())
                .put("offsetY", op.offsetY())
                .put("data", new JsonArray(result));
    }

    private static JsonObject toJson(AllCreatures op) {
        return new JsonObject()
                .put("action", "all-creatures")
                .put("width", op.width())
                .put("height", op.height())
                .put("offsetX", op.offsetX())
                .put("offsetY", op.offsetY())
                .put("data", toJson(op.creatures()));
    }


    static JsonArray toJson(int[] array) {
        var list = toList(array);
        return new JsonArray(list);
    }

    static JsonArray toJson(short[] array) {
        var list = toList(array);
        return new JsonArray(list);
    }

    static List<Integer> toList(int[] array) {
        var list = new ArrayList<Integer>(array.length);

        for (int i : array) {
            list.add(i);
        }

        return list;
    }

    static List<Short> toList(short[] array) {
        var list = new ArrayList<Short>(array.length);

        for (short i : array) {
            list.add(i);
        }

        return list;
    }
}
