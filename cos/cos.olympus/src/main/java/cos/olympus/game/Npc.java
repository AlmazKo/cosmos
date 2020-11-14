package cos.olympus.game;

public record Npc(
        @Override int id,
        @Override String name,
        @Override int x,
        @Override int y
) implements Avatar {
}
