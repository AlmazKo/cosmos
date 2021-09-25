package cos.map;

public enum NpcType {
    PLAYER, SHEEP, WOLF;

    public boolean isAggressive() {
        return this == WOLF;
    }
}
