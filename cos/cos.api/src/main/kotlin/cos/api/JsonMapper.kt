package cos.api

import cos.ops.AnyOp
import cos.ops.Appear
import cos.ops.ObjAppear
import io.vertx.core.json.JsonObject

object JsonMapper {


    fun toJson(op: AnyOp): JsonObject {

        return when (op) {
            is Appear -> toJ(op)
            is ObjAppear -> toJ(op)
            else -> throw RuntimeException("Unknown $op")
        }
    }

    fun toJ(op: Appear) = JsonObject()
        .put("id", op.id())
        .put("action", "appear")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("x", op.x())
                .put("y", op.y())
                .put("mv", op.mv())
                .put("sight", op.sight())
        )

    fun toJ(op: ObjAppear) = JsonObject()
        .put("id", op.id())
        .put("action", "appear_obj")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("x", op.x())
                .put("y", op.y())
                .put("tileId", op.tileId())
        )
}
