package cos.olympus.game

data class Metrics(
    val actorId: Int,
    var lvl: Int,
    var life: Int,
    var maxLife: Int,
    var exp: Int
) {
    constructor(actorId: Int, life: Int) : this(actorId, 1, life, life, 0)




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
}
