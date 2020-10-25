package cos.olympus.game;


import cos.ops.Direction;
import org.jetbrains.annotations.Nullable;

interface VectorObject {
    int x();

    int y();

    int offset();

    int speed();

    @Nullable Direction dir();

//    fun toLong(): Long {
//        //todo: implement it
//        return 0L
//    }
}
