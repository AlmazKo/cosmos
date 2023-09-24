package cos.olympus.game;

import cos.map.CreatureType;

public record Player(
        @Override int id,
        @Override String name
) implements Avatar {

    @Override
    public CreatureType type() {
        return CreatureType.PLAYER;
    }
}
