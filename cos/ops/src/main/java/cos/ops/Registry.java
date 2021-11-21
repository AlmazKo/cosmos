package cos.ops;


import cos.ops.in.*;
import cos.ops.out.*;
import cos.ops.parser.Error;
import cos.ops.parser.Unknown;
import cos.ops.parser.*;

public interface Registry {
    int ERROR_OP = 9;
    byte NOPE_OP = Op.NOPE;

    OpParser PARSER = new OpParser.Builder()
            .register(ERROR_OP, Error.class)
            .register(NOPE_OP, Nope.class)
            .register(Op.DISCONNECT, Disconnect.class)
            .register(Op.FORCED_EXIT, ForcedExit.class)
            .register(Op.LOGOUT, Logout.class)
            .register(10, HeartBeat.class)
            .register(11, Unknown.class)


            .register(Op.LOGIN, Login.class)
            .register(Op.APPEAR, Appear.class)
            .register(Op.PROTO_APPEAR, ProtoAppear.class)
            .register(Op.MOVE, Move.class)
            .register(Op.STOP_MOVE, StopMove.class)
//            .register(Op.LOGOUT, .class)
            .register(Op.MOVE_STOP, StopMove.class)
            .register(Op.APPEAR_OBJ, ObjAppear.class)
            .register(Op.CREATURE_MOVED, CreatureMoved.class)
            .register(Op.CREATURE_HID, CreatureHid.class)
            .register(Op.EMMIT_FIREBALL, FireballEmmit.class)
            .register(Op.FIREBALL_MOVED, FireballMoved.class)
            .register(Op.DAMAGE, Damage.class)
            .register(Op.DEATH, Death.class)
            .register(Op.MELEE_ATTACK, MeleeAttack.class)
            .register(Op.MELEE_ATTACKED, MeleeAttacked.class)
            .register(Op.METRICS, Metrics.class)
            .register(Op.EMMIT_SHOT, ShotEmmit.class)
            .register(Op.SHOT_MOVED, ShotMoved.class)
            .register(99, UserPackage.class)
            .register(110, AllCreatures.class)

            .build();
}
