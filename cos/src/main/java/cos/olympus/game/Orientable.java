package cos.olympus.game;

import cos.ops.Direction;
import org.jetbrains.annotations.Nullable;

public interface Orientable extends Placeable {
    @Override
    int x();

    @Override
    int y();

    int speed();

    int offset();

    @Nullable Direction mv();

    Direction sight();
}
