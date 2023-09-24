package cos.ops;

public interface Op {
    byte ENTER = 99;
    byte LOGIN = 100;
    byte APPEAR = 101;
    byte MOVE = 102;
    byte STOP_MOVE = 103;
    byte MOVE_STOP = 105;
    byte APPEAR_OBJ = 106;
    byte CREATURE_MOVED = 107;
    byte CREATURE_HID = 108;
    byte EMMIT_FIREBALL = 109;
    byte FIREBALL_MOVED = 110;
    byte DAMAGE = 111;
    byte DEATH = 112;
    byte MELEE_ATTACK = 113;
    byte MELEE_ATTACKED = 114;
    byte METRICS = 115;
    byte EMMIT_SHOT = 116;
    byte SHOT_MOVED = 117;
    byte PROTO_APPEAR = 118;
    byte WORLD_LOGIN = 119;
    byte TELEPORT_IN = 120;

    //infrastructure op-codes
    byte NOPE = 0;
    byte DISCONNECT = 1;
    byte LOGOUT = 2;
    byte FORCED_EXIT = 3;
}
