package cos.ops;

public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    public Direction opposite() {
        int ord = ordinal();
        return Direction.values()[(ord % 2 == 0) ? ord + 1 : ord - 1];
    }


}
