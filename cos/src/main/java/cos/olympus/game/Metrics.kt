package cos.olympus.game

class Metrics(
    val creatureId: Int,
    var lvl: Int,
    var life: Int,
    var maxLife: Int,
    var exp: Int
) {
    constructor(creatureId: Int, life: Int) : this(creatureId, 1, life, life, 0)

    fun maxLife(): Int {
        return maxLife
    }

    fun life(): Int {
        return life
    }

    fun plus(amount: Int) {
        this.life += amount
        if (life > maxLife) life = maxLife
    }

    fun minus(amount: Int) {
        this.life -= amount
        if (life < 0) life = 0
    }

    val isDead: Boolean
        get() = life <= 0


    fun copy(): Metrics {
        return Metrics(creatureId, lvl, life, maxLife, exp)
    }
}
