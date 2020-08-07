package cos.ops;

public interface Op {
    byte LOGIN     = 100;
    byte APPEAR    = 101;
    byte MOVE      = 102;
    byte STOP_MOVE = 103;

    //infrastructure op-codes
    byte NOPE       = 0;
    byte DISCONNECT = 1;
}
