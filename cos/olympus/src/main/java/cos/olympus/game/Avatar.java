package cos.olympus.game;

import cos.map.NpcType;

public interface Avatar extends Placeable {

    int id();

    NpcType type();
    String name();

    int x();

    int y();
}
