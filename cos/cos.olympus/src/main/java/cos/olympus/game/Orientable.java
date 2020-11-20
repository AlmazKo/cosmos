package cos.olympus.game;

import cos.ops.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Orientable{
    int x();

    int y();

    int speed();

    int offset();

    @Nullable Direction mv();

    Direction sight();

}
