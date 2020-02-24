//package cos.olympus.game.spells
//
//import cos.olympus.Tsm
//import cos.olympus.game.ActionConsumer
//import cos.olympus.game.Creature
//import cos.olympus.game.GameMap
//import cos.olympus.game.actions.SpellAction
//
//interface SpellStrategy {
//    val action: SpellAction
//    val id get() = action.id
//    fun inZone(creature: Creature): Boolean
//    fun handle(time: Tsm, actions: ActionConsumer, map: GameMap): Boolean
//}
