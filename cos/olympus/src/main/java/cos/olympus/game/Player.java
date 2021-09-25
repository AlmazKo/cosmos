package cos.olympus.game;

import cos.map.CreatureType;

final class Player implements Avatar {
    final int id;
    final String name;
    final int lastX;
    final int lastY;

    public Player(int id, String name) {
        this(id, name, 0, 0);
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

    @Override public CreatureType type() {
        return CreatureType.PLAYER;
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
