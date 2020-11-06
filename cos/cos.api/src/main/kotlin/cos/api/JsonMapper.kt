package cos.api

import cos.ops.AnyOp
import cos.ops.Appear
import cos.ops.CreatureHid
import cos.ops.CreatureMoved
import cos.ops.ObjAppear
import io.vertx.core.json.JsonObject

object JsonMapper {


    fun toJson(op: AnyOp): JsonObject {

        return when (op) {
            is Appear -> toJ(op)
            is ObjAppear -> toJ(op)
            is CreatureMoved -> toJ(op)
            is CreatureHid -> toJ(op)
            else -> throw RuntimeException("Unknown $op")
        }
    }

    private fun toJ(op: Appear) = JsonObject()
        .put("id", op.id())
        .put("action", "appear")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("userId", op.userId())
                .put("x", op.x())
                .put("y", op.y())
                .put("mv", op.mv())
                .put("sight", op.sight())
        )

    private fun toJ(op: ObjAppear) = JsonObject()
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

    private fun toJ(op: CreatureMoved) = JsonObject()
        .put("id", op.id())
        .put("action", "creature_moved")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("creatureId", op.creatureId())
                .put("x", op.x())
                .put("y", op.y())
                .put("speed", op.speed())
                .put("mv", op.mv())
                .put("sight", op.sight())
        )

    private fun toJ(op: CreatureHid) = JsonObject()
        .put("id", op.id())
        .put("action", "creature_hid")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("creatureId", op.creatureId())
        )
}
