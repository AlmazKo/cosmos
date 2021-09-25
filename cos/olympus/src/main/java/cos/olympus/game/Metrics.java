package cos.olympus.game;

public class Metrics {
    public final int creatureId;
    public int life;
    public int maxLife;

    public Metrics(int creatureId, int life) {
        this(creatureId, life, life);
    }

    public Metrics(int creatureId, int life, int maxLife) {
        this.creatureId = creatureId;
        this.life = life;
        this.maxLife = maxLife;
    }

    public int maxLife() {
        return maxLife;
    }

    public int life() {
        return life;
    }

    void plus(int amount) {
        this.life += amount;
        if (life > maxLife) life = maxLife;
    }

    void minus(int amount) {
        this.life -= amount;
        if (life < 0) life = 0;
    }

    public boolean isDead() {
        return life <= 0;
    }


    public Metrics copy() {
        return new Metrics(creatureId, life, maxLife);
    }
}
