package cos.olympus.game;

import cos.map.TileType;
import org.jetbrains.annotations.Nullable;

public interface GMap {
    @Nullable TileType get(int x, int y);

    int getObject(int x, int y);

    @Nullable Creature getCreature(int x, int y);

    boolean isNoCreatures(int x, int y);

    Creature createCreature(User usr);
    void moveCreature(int fromX, int fromY, int toX, int toY);
}
