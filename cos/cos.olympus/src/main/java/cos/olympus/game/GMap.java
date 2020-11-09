package cos.olympus.game;

import cos.map.TileType;
import org.jetbrains.annotations.Nullable;

public interface GMap {
    @Nullable TileType get(int x, int y);

    @Nullable Obj getObject(int x, int y);

    @Nullable Creature getCreature(int x, int y);

    boolean isNoCreatures(int x, int y);

    default boolean hasCreature(int x, int y) {
        return !isNoCreatures(x, y);
    }

    Creature createCreature(User usr);

    void moveCreature(int fromX, int fromY, int toX, int toY);

    void moveCreature(Creature cr, int toX, int toY);
}
