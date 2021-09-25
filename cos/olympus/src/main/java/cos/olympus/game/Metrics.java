package cos.olympus.game;

public class Metrics {
    public final int creatureId;
    public int lvl = 1;
    public int life;
    public int maxLife;
    public int exp;

    public Metrics(int creatureId, int life) {
        this(creatureId, 1, life, life, 0);
    }

    public Metrics(int creatureId, int lvl, int life, int maxLife, int exp) {
        this.creatureId = creatureId;
        this.life = life;
        this.maxLife = maxLife;
        this.exp = exp;
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
        return new Metrics(creatureId, lvl, life, maxLife, exp);
    }
}
