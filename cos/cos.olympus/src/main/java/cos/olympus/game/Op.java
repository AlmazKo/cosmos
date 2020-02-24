package cos.olympus.game;

public interface Op {
    byte LOGIN = 1;
    byte APPEAR = 2;
    byte MOVE = 3;
    byte STOP_MOVE = 4;



    //infrastructure op-codes
    byte NOPE = 0;
    byte FINISH = 127;
}
