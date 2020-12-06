package cos.map;

public enum ObjectType {
    NOTHING(0),
    ROCK(1),
    BRIDGE(2),
    BUSH(3),
    TALL_GRASS(4),
    GATE(5);
    private final byte id;

    private ObjectType(int id) {

        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
