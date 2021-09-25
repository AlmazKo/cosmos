package cos.olympus.game;

import cos.map.NpcType;

public record Npc(
        @Override int id,
        @Override   NpcType type,
        @Override String name,
        @Override int x,
        @Override int y
) implements Avatar {
}
