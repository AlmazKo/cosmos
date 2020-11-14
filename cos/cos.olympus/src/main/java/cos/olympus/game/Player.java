package cos.olympus.game;

final class Player implements Avatar {
    final int    id;
    final String name;
    int lastX;
    int lastY;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Player(int id, String name, int lastX, int lastY) {
        this.id = id;
        this.name = name;
        this.lastX = lastX;
        this.lastY = lastY;
    }

    @Override public int id() {
        return id;
    }

    @Override public String name() {
        return name;
    }

    @Override public int x() {
        return lastX;
    }

    @Override public int y() {
        return lastY;
    }
}
