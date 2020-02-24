package cos.olympus.game;

public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
        };
    }
}
