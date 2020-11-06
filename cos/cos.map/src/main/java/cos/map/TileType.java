package cos.map;

public enum TileType {
    NOTHING(0),
    GRASS(1),
    SAND(2),
    LAVA(3),
    SHALLOW(4),
    DEEP_WATER(5),
    ICE(6),
    SNOW(7),
    ROAD(8),
    GATE(9),
    WALL(10),
    ;

    private final byte id;

    private TileType(int id) {

        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
