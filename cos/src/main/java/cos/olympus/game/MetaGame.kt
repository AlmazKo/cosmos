package cos.olympus.game

import cos.logging.Logger
import cos.olympus.game.strategy.LoginStrategy
import cos.olympus.game.strategy.Strategy
import cos.olympus.game.strategy.TeleportInStrategy
import cos.olympus.util.OpConsumer
import cos.ops.ServiceOp
import cos.ops.UserOp
import cos.ops.`in`.Login
import cos.ops.out.AllCreatures
import cos.ops.out.TeleportIn
import java.util.function.Consumer

class MetaGame(private val games: Map<String, Game>) {
    private val users: MutableMap<Int, Usr> = HashMap()
    private val strategies: MutableList<Strategy> = ArrayList()

    fun onTick(tick: Int, userOps: List<UserOp>, serviceOps: List<ServiceOp>, out: OpConsumer) {
        serviceOps.forEach { op ->
            if (op is TeleportIn) {
                val target = games[op.world()]!!
                strategies.add(TeleportInStrategy(tick, op, target))
            }
        }

        userOps.forEach { op ->
            if (op is Login) {
//                strategies.add(new LoginStrategy(games, op.userId()));
                onLogin(tick, (op as Login?)!!)
            } else {
                games.values.forEach { it.onOp(op) }
            }
        }

        games.values.forEach(Consumer { game: Game ->
            game.onTick(tick, out)
            collectMetrics(out, game)
        })

        strategies.removeIf { strategy: Strategy -> strategy.onTick(tick, out) }
    }

    private fun collectMetrics(out: OpConsumer, game: Game) {
        val crs = game.world.allCreatures
        if (crs.isEmpty()) return

        var i = 0
        val data = IntArray(crs.size * 3)
        for (cr in crs) {
            data[i++] = cr.y
            data[i++] = cr.y
            data[i++] = cr.type.ordinal
        }
        val w = game.world
        val op = AllCreatures(w.width, w.height, w.offsetX, w.offsetY, data)
        out.add(op)
    }

    private fun onLogin(tick: Int, op: Login) {
        var usr = users[op.userId]
        if (usr == null) {
            usr = Usr(op.userId, "castle-island")
            users[op.userId] = usr
            LOG.info("New User $usr")
            strategies.add(LoginStrategy(games, usr))
        }
    }

    companion object {
        private val LOG: Logger = Logger.get(Game::class.java)
    }
}
