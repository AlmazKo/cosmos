package cos.olympus.game;

import cos.ops.Direction;

public record Orientation(
        int x,
        int y,
        int speed,
        int offset,
        Direction mv
) implements VectorObject {

}
