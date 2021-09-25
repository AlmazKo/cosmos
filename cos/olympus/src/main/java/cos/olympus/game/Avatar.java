package cos.olympus.game;

import cos.map.CreatureType;

public interface Avatar extends Placeable {

    int id();

    CreatureType type();
    String name();

    int x();

    int y();
}
