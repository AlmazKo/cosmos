package cos.map;

public class Lands {
    private final int   width;
    private final int   height;
    private final int     offsetX;
    private final int     offsetY;
    private final short[] basis;
    private final short[] objects;
    private final Tile[]  tiles;


    public Lands(int width, int height, int offsetX, int offsetY, short[] basis, short[] objects, Tile[] tiles) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.basis = basis;
        this.objects = objects;
        this.tiles = tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public short[] getBasis() {
        return basis;
    }

    public short[] getObjects() {
        return objects;
    }

    public Tile[] getTiles() {
        return tiles;
    }
}
