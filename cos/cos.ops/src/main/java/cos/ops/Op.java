package cos.ops;

public interface Op {
    byte LOGIN      = 100;
    byte APPEAR     = 101;
    byte MOVE       = 102;
    byte STOP_MOVE  = 103;
    byte LOGOUT     = 104;
    byte MOVE_STOP  = 105;
    byte APPEAR_OBJ = 106;
    byte CREATURE_MOVED = 107;
    byte CREATURE_HID = 108;

    //infrastructure op-codes
    byte NOPE       = 0;
    byte DISCONNECT = 1;
    byte EXIT = 2;
}
