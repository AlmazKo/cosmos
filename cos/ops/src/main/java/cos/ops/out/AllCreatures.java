package cos.ops.out;

import cos.ops.SomeOp;

public record AllCreatures(
        int width,
        int height,
        int offsetX,
        int offsetY,
        int[] creatures
) implements SomeOp {

    @Override
    public String toString() {
        return "AllCreatures{" +
                "width=" + width +
                ", height=" + height +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", creatures=" + creatures.length +
                '}';
    }
}
