package cos.olympus.game;

public interface Placeable {
    int x();

    int y();

    static Placeable sample(int x, int y) {
        return new Placeable() {
            @Override public int x() {
                return x;
            }

            @Override public int y() {
                return y;
            }
        };
    }
}
