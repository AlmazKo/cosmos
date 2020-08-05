package cos.api

import cos.ops.AnyOp
import cos.ops.Arrival
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

object JsonMapper {


    fun toJson(op: AnyOp): JsonObject {

        if (op is Arrival) {
            val js = JsonObject()
                .put("id", op.id())
                .put("action", "appear")
                .put("type", "")
                .put(
                    "data", JsonObject()
                        .put("x", op.x())
                        .put("y", op.y())
                        .put("dir", op.dir().ordinal)
                        .put("sight", op.sight().ordinal)
                )

            return JsonObject()
                .put("tick", 1)
                .put("time", System.currentTimeMillis() / 1000)
                .put("messages", JsonArray().add(js))
        }


        throw RuntimeException("Unknown op $op")
    }
}
