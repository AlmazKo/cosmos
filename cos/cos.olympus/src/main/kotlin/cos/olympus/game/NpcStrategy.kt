package cos.olympus.game

import cos.map.TileType
import cos.olympus.Tsm
import cos.olympus.game.actions.Step
import kotlin.random.Random

class NpcStrategy(
    private val npc: Creature,
    private val map: GameMap
) {
    private var nextPlannedTime = -1L

//    fun isDead() = npc.isDead

    private fun passive(time: Tsm): Step? {

        return null
//        var dir: Direction
//        var attemtps = 0
//        do {
//            //fixme infinity loop
//            dir = Direction.values()[Random.nextInt(0, 4)]
//            ++attemtps
//        } while (!canStep(dir) && attemtps < 5)
//
//        if (attemtps >= 5) {
//            //println("Fail get direction" + npc.state)
//            return null
//        }
//
//        return Step(npc.state.x, npc.state.y, time, dir, 800, npc)
    }


    private fun canStep(dir: Direction): Boolean {

        var x = npc.x
        var y = npc.y

//        when (dir) {
//            Direction.NORTH -> y--
//            Direction.SOUTH -> y++
//            Direction.WEST -> x--
//            Direction.EAST -> x++
//        }
//
//        val tile = map[x, y]
//        if (tile !== null) {
//            if (tile.type.isSteppable()) {
//                if (map.isNoCreatures(x, y)) {
//                    val obj = map.getObject(x, y)
//                    //mobs can't move through gates
//                    if (obj !== null && (!obj.type.isSteppable() || obj.type === TileType.GATE)) return false;
//                    return true
//                }
//            }
//        }

        return false
    }


    fun onTick(time: Tsm, consumer: ActionConsumer) {
        if (time > nextPlannedTime) {


            val step = passive(time) ?: return

            consumer.add(step)
            nextPlannedTime = time + Random.nextInt(1000, 2000)
        }
    }

}


//fun TileType?.isSteppable() = this !== TileType.WALL && this !== TileType.WATER && this !== TileType.NOTHING
fun TileType?.isSteppable() =  this !== TileType.NOTHING
