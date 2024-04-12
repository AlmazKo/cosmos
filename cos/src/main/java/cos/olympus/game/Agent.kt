package cos.olympus.game;

import cos.map.CreatureType;

public interface Agent extends Orientable {
    int id();

    CreatureType type();

    default boolean is(CreatureType type) {
        return this.type() == type;
    }
}
