package cos.map;

public enum ActorType {
    PLAYER, SHEEP, WOLF;

    public boolean isAggressive() {
        return this == WOLF;
    }
}
