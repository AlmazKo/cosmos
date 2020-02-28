package cos.map;

public final class Tile {
    private final int id;
    private final TileType type;

    public Tile(int id, TileType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public TileType getType() {
        return type;
    }
}
