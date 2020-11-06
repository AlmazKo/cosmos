package cos.olympus.game;

final class User {
    final int    id;
    final String name;
    int lastX;
    int lastY;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(int id, String name, int lastX, int lastY) {
        this.id = id;
        this.name = name;
        this.lastX = lastX;
        this.lastY = lastY;
    }
}
