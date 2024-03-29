package cos.api

import cos.ops.AnyOp
import cos.ops.out.*
import io.vertx.core.json.JsonObject

object JsonMapper {


    fun toJson(op: AnyOp): JsonObject {

        return when (op) {
            is Appear -> toJ(op)
            is ProtoAppear -> toJ(op)
            is ObjAppear -> toJ(op)
            is Metrics -> toJ(op)
            is CreatureMoved -> toJ(op)
            is ShotMoved -> toJ(op)
            is CreatureHid -> toJ(op)
            is FireballMoved -> toJ(op)
            is Damage -> toJ(op)
            is Death -> toJ(op)
            is MeleeAttacked -> toJ(op)
            else -> throw RuntimeException("No json serializator for $op")
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
//                .put("map", "map")
                .put("x", op.x())
                .put("y", op.y())
                .put("mv", op.mv())
                .put("sight", op.sight())
                .put("lvl", op.lvl())
                .put("life", op.life())
        )

    private fun toJ(op: ProtoAppear) = JsonObject()
        .put("id", op.id())
        .put("action", "proto_appear")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("userId", op.userId())
                .put("map", op.world())
                .put("x", op.x())
                .put("y", op.y())
                .put("sight", op.sight())
        )

    private fun toJ(op: Metrics) = JsonObject()
        .put("id", op.id())
        .put("action", "metrics")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("userId", op.userId())
                .put("creatureId", op.creatureId())
                .put("life", op.life())
                .put("lvl", op.lvl())
                .put("exp", op.exp())
                .put("maxLife", op.maxLife())
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
                .put("offset", op.offset())
                .put("speed", op.speed())
                .put("mv", op.mv())
                .put("sight", op.sight())
        )

    private fun toJ(op: FireballMoved) = JsonObject()
        .put("id", op.id())
        .put("action", "fireball_moved")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("spellId", op.spellId())
                .put("sourceId", op.userId())
                .put("x", op.x())
                .put("y", op.y())
                .put("speed", op.speed())
                .put("dir", op.dir())
                .put("finished", op.finished())
        )

    private fun toJ(op: ShotMoved) = JsonObject()
        .put("id", op.id())
        .put("action", "shot_moved")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("spellId", op.spellId())
                .put("sourceId", op.userId())
                .put("x", op.x())
                .put("y", op.y())
                .put("speed", op.speed())
                .put("dir", op.dir())
                .put("finished", op.finished())
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

    private fun toJ(op: Damage) = JsonObject()
        .put("id", op.id())
        .put("action", "damage")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("creatureId", op.creatureId())
                .put("victimId", op.victimId())
                .put("amount", op.amount())
                .put("crit", op.crit())
                .put("spellId", op.spellId())
        )

    private fun toJ(op: MeleeAttacked) = JsonObject()
        .put("id", op.id())
        .put("action", "melee_attacked")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("creatureId", op.sourceId())
                .put("spellId", op.spellId())
        )

    private fun toJ(op: Death) = JsonObject()
        .put("id", op.id())
        .put("action", "death")
        .put("type", "")
        .put(
            "data", JsonObject()
                .put("id", op.id())
                .put("creatureId", op.creatureId())
                .put("victimId", op.victimId())
        )
}
