package cos.olympus.game;

import cos.map.CreatureType;

public record Npc(
        @Override int id,
        @Override CreatureType type,
        @Override String name,
        @Override int x,
        @Override int y
) implements Avatar {
}
