package cos.olympus.game;

import cos.map.TileType;
import org.jetbrains.annotations.Nullable;

public interface TileMap {
    @Nullable TileType get(int x, int y);
}
