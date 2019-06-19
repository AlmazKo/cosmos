package cos.olympus.game.actions


interface SpellAction : Action {
    val id: Long
    var finished: Boolean
    fun inZone(objX: Int, objY: Int, radius: Int): Boolean
}
