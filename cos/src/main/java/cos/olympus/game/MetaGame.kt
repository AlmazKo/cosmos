package cos.olympus.game

import cos.logging.Logger
import cos.olympus.game.strategy.LoginStrategy
import cos.olympus.game.strategy.Strategy
import cos.olympus.game.strategy.TeleportInStrategy
import cos.olympus.util.OpConsumer
import cos.olympus.util.Ops
import cos.ops.ServiceOp
import cos.ops.UserOp
import cos.ops.`in`.Login
import cos.ops.out.AllActors
import cos.ops.out.TeleportIn

class MetaGame(private val games: Map<String, Game>) {
    private val users = HashMap<Int, User>()
    private val strategies = ArrayList<Strategy>()
    private val defaultWorld = "castle-island"

    fun onTick(tick: TickId, userOps: List<UserOp>, serviceOps: List<ServiceOp>, out: OpConsumer) {

        games.values.forEach { it.onTick(tick, out) }

        serviceOps.forEach { op ->
            if (op is TeleportIn) {
                val target = games[op.world()]!!
                strategies.add(TeleportInStrategy(tick, op, target))
            }
        }

        userOps.forEach { op ->
            Ops.LOGGER.info(op, "in")
            if (op is Login) {
                onLogin(tick, op)
            } else {
                games.values.forEach { it.onOp(op) }
            }
        }

        games.values.forEach {
            it.process()
            collectMetrics(out, it)
        }

        strategies.removeIf { it.onTick(tick, out) }
    }

    private fun collectMetrics(out: OpConsumer, game: Game) {
        val actors = game.world.allActors
        if (actors.isEmpty()) return

        var i = 0
        val data = IntArray(actors.size * 3)
        for (a in actors) {
            data[i++] = a.x
            data[i++] = a.y
            data[i++] = a.type.ordinal
        }
        val w = game.world
        val op = AllActors(w.width, w.height, w.offsetX, w.offsetY, data)
        out.add(op)
    }

    private fun onLogin(tick: TickId, op: Login) {
        var usr = users[op.userId]
        if (usr == null) {
            usr = User(op.userId, defaultWorld)
            users[op.userId] = usr
            LOG.info(usr, "new")
            strategies.add(LoginStrategy(games, usr))
        }
    }

    companion object {
        private val LOG: Logger = Logger.get(Game::class.java)
    }
}
