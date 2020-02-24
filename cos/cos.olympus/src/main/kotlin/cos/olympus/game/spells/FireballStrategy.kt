//package cos.olympus.game.spells
//
//import cos.olympus.Tsm
//import cos.olympus.game.ActionConsumer
//import cos.olympus.game.Creature
//import cos.olympus.game.GameMap
//import cos.olympus.game.actions.Damage
//import cos.olympus.game.actions.Death
//import cos.olympus.game.actions.Fireball
//
//class FireballStrategy(override val action: Fireball) : SpellStrategy {
//
//    override fun inZone(creature: Creature): Boolean {
//        return false
////        return action.inZone(creature.x, creature.y, creature.viewDistance)
//    }
//
//    override fun handle(time: Tsm, actions: ActionConsumer, map: GameMap): Boolean {
//
//        return false
////        val distance = Math.min(action.distance, Math.round((time - action.time) / action.speed.toFloat()))
////        action.distanceTravelled = distance
////
////        val x = action.currentX
////        val y = action.currentY
////
////        val victim = map.getCreature(x, y)
////        if (victim !== null && victim.id != action.source.id) {
////            val d = Damage(x, y, time, victim, action.source, 25, action.id)
////            victim.damage(d)
////            actions.add(d)
////
////            if (victim.state.life == 0) {
////                actions.add(Death(d))
////            }
////
////            action.finished = true
////        }
////
////        if (distance >= action.distance) {
////            action.finished = true
////        }
////
////        return action.finished
//    }
//
//}
