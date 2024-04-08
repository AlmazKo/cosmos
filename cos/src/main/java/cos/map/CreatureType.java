package cos.map;

public enum CreatureType {
    PLAYER, SHEEP, WOLF;

    public boolean isAggressive() {
        return this == WOLF;
    }
}
