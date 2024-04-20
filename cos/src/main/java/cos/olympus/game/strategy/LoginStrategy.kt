package cos.olympus.game.strategy

import cos.olympus.game.Game
import cos.olympus.game.Player
import cos.olympus.game.User
import cos.olympus.util.OpConsumer
import cos.ops.out.ProtoAppear

class LoginStrategy(
    private val games: Map<String, Game>,
    private val usr: User
) : Strategy {

    override fun onTick(tick: Int, out: OpConsumer): Boolean {
        val world = games[usr.worldName]!!.world
        val player = Player(usr.id, usr.name)
        val actor = world.place(player, 0, 0, 100, 4)
        val op = ProtoAppear(1, usr.id, "castle-island", actor.x, actor.y, actor.sight)
        out.add(op)
        return true
    }
}
