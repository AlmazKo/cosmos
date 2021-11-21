package cos.api;

import cos.ops.SomeOp;
import cos.ops.out.Appear;
import cos.ops.out.CreatureHid;
import cos.ops.out.CreatureMoved;
import cos.ops.out.Damage;
import cos.ops.out.Death;
import cos.ops.out.FireballMoved;
import cos.ops.out.MeleeAttacked;
import cos.ops.out.Metrics;
import cos.ops.out.ObjAppear;
import cos.ops.out.ProtoAppear;
import cos.ops.out.ShotMoved;
import io.vertx.core.json.JsonObject;

public class JsonMapper {

    public static JsonObject toJson(SomeOp op) {
        return switch (op) {
            case Appear o -> toJ(o);
            case ProtoAppear o -> toJ(o);
            case ObjAppear o -> toJ(o);
            case Metrics o -> toJ(o);
            case CreatureMoved o -> toJ(o);
            case ShotMoved o -> toJ(o);
            case CreatureHid o -> toJ(o);
            case FireballMoved o -> toJ(o);
            case Damage o -> toJ(o);
            case Death o -> toJ(o);
            case MeleeAttacked o -> toJ(o);
            default -> throw new RuntimeException("No json serializator for $op");
        };
    }

    private static JsonObject toJ(Appear op) {

        return new JsonObject().put("id", op.id())
                .put("action", "appear")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("userId", op.userId())
//                .put("map", "map")
                                .put("x", op.x())
                                .put("y", op.y())
                                .put("mv", op.mv())
                                .put("sight", op.sight())
                                .put("lvl", op.lvl())
                                .put("life", op.life())
                );
    }

    private static JsonObject toJ(ProtoAppear op) {
        return new JsonObject().put("id", op.id())
                .put("action", "proto_appear")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("userId", op.userId())
                                .put("map", op.world())
                                .put("x", op.x())
                                .put("y", op.y())
                                .put("sight", op.sight())
                );
    }

    private static JsonObject toJ(Metrics op) {
        return new JsonObject().put("id", op.id())
                .put("action", "metrics")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("userId", op.userId())
                                .put("creatureId", op.creatureId())
                                .put("life", op.life())
                                .put("lvl", op.lvl())
                                .put("exp", op.exp())
                                .put("maxLife", op.maxLife())
                );
    }

    private static JsonObject toJ(ObjAppear op) {
        return new JsonObject().put("id", op.id())
                .put("action", "appear_obj")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())

                                .put("x", op.x())
                                .put("y", op.y())
                                .put("tileId", op.tileId())
                );
    }

    private static JsonObject toJ(CreatureMoved op) {
        return new JsonObject()
                .put("id", op.id())
                .put("action", "creature_moved")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("creatureId", op.creatureId())
                                .put("x", op.x())
                                .put("y", op.y())
                                .put("offset", op.offset())
                                .put("speed", op.speed())
                                .put("mv", op.mv())
                                .put("sight", op.sight())
                );
    }

    private static JsonObject toJ(FireballMoved op) {
        return new JsonObject()
                .put("id", op.id())
                .put("action", "fireball_moved")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("spellId", op.spellId())
                                .put("sourceId", op.userId())
                                .put("x", op.x())
                                .put("y", op.y())
                                .put("speed", op.speed())
                                .put("dir", op.dir())
                                .put("finished", op.finished())
                );
    }

    private static JsonObject toJ(ShotMoved op) {
        return new JsonObject()
                .put("id", op.id())
                .put("action", "shot_moved")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("spellId", op.spellId())
                                .put("sourceId", op.userId())
                                .put("x", op.x())
                                .put("y", op.y())
                                .put("speed", op.speed())
                                .put("dir", op.dir())
                                .put("finished", op.finished())
                );
    }

    private static JsonObject toJ(CreatureHid op) {
        return new JsonObject()
                .put("id", op.id())
                .put("action", "creature_hid")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("creatureId", op.creatureId())
                );
    }

    private static JsonObject toJ(Damage op) {
        return new JsonObject()
                .put("id", op.id())
                .put("action", "damage")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("creatureId", op.creatureId())
                                .put("victimId", op.victimId())
                                .put("amount", op.amount())
                                .put("crit", op.crit())
                                .put("spellId", op.spellId())
                );
    }

    private static JsonObject toJ(MeleeAttacked op) {
        return new JsonObject()
                .put("id", op.id())
                .put("action", "melee_attacked")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("creatureId", op.sourceId())
                                .put("spellId", op.spellId())
                );
    }

    private static JsonObject toJ(Death op) {
        return new JsonObject()
                .put("id", op.id())
                .put("action", "death")
                .put("type", "")
                .put(
                        "data", new JsonObject()
                                .put("id", op.id())
                                .put("creatureId", op.creatureId())
                                .put("victimId", op.victimId())
                );
    }
}
