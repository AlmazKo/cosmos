package cos.olympus.game

import cos.olympus.Tsm
import cos.olympus.game.actions.Death
import cos.olympus.game.actions.ReSpawn

class PlayerReSpawnStrategy(
    private val death: Death
) {


    private var respawnTime = death.time + 5000

//    fun onTick(time: Tsm, consumer: ActionConsumer, map: GameMap): Boolean {
//
//        if (time < respawnTime) {
//            return false
//        }
//
//        val p = map.addPlayer(death.victim.id)
//        consumer.add(ReSpawn(time, p))
//
//        return true
//    }
//

    companion object {
        private var creaturesInc = 1000
    }
}
