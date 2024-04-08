package cos.ops;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public Direction opposite() {
        int ord = ordinal();
        return Direction.values()[(ord % 2 == 0) ? ord + 1 : ord - 1];
    }

    //todo add @contract &  @Nullable

    public static Direction of(byte id) {
        if (id == -1) return null;
        return Direction.values()[id];
    }


    public boolean isX() {
        return this == WEST || this == EAST;
    }

    public boolean isY() {
        return this == NORTH || this == SOUTH;
    }
}
