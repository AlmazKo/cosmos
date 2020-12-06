package cos.olympus.game;

import cos.map.Tile;

public record Obj(
        int id,
        Tile tile,
        int x,
        int y
) {
}
