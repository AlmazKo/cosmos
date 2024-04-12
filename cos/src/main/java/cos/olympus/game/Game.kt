package cos.olympus.game

import cos.logging.Logger
import cos.map.Coord
import cos.olympus.game.events.Damage
import cos.olympus.game.events.Death
import cos.olympus.game.strategy.RespawnPlayerStrategy
import cos.olympus.game.strategy.RespawnStrategy
import cos.olympus.game.strategy.Strategy
import cos.olympus.game.strategy.TeleportOutStrategy
import cos.olympus.util.OpConsumer
import cos.olympus.util.OpsAggregator
import cos.ops.UserOp
import cos.ops.`in`.FireballEmmit
import cos.ops.`in`.Logout
import cos.ops.`in`.MeleeAttack
import cos.ops.`in`.Move
import cos.ops.`in`.ShotEmmit
import cos.ops.`in`.StopMove
import cos.ops.out.Disconnect

class Game(@JvmField val world: World) {
    data class Config(val settleMobs: Boolean)

    private val cfg = Config(true)

    private val movements = Movements(world)
    private val spells = Spells(world)
    private val npcRespawns = ArrayList<RespawnStrategy>()
    private val playersRespawns = ArrayList<RespawnPlayerStrategy>()
    private val strategies = ArrayList<Strategy>()
    private val damages = Damages()
    private val deaths = ArrayList<Death>() //todo: channel
    private val zone = Zone(world)
    private var tickOuts: OpConsumer = OpsAggregator()

    private var tickId = 0

    init {
        if (cfg.settleMobs) settleMobs()
    }

    private fun settleMobs() {
        world.respawns.forEach { resp ->
            for (i in 0 until resp.size) {
                npcRespawns.add(RespawnStrategy(world, spells, movements, Coord(resp.x, resp.y), resp.type))
            }
        }
    }

    fun onOp(op: UserOp) {
        LOG.info(op, "game_in")

        try {
            when (op) {
                is Logout -> removeAvatar(op.userId())
                is Move -> movements.onMove(op)
                is StopMove -> movements.onStopMove(op)

                is FireballEmmit -> spells.onSpell(tickId, op)
                is ShotEmmit -> spells.onShot(tickId, op)
                is MeleeAttack -> spells.onMeleeAttack(tickId, op)
                else -> throw IllegalStateException("Unexpected user op: $op")
            }
        } catch (e: Exception) {
            LOG.warn("Error during processing $op", e)
            tickOuts.add(Disconnect(op.id(), tickId, op.userId()))
        }
    }


    fun onTick(tick: Int, out: OpConsumer) {
        tickId = tick
        tickOuts = out

        strategies.removeIf { it: Strategy -> it.onTick(tick, out) }
        playersRespawns.removeIf { it: RespawnPlayerStrategy -> it.onTick(tick, out) }
        movements.onTick(tick)
        damages.onTick(tick)

        spells.onTick(tick, damages, out)
        damages.forEach(::onDamage)
        npcRespawns.forEach { it.onTick(tick, out) }
        world.allCreatures.forEach { zone.onTick(it, tick, out) }
        world.allCreatures.forEach(::checkPortals)
        notifyAboutEvents()

        spells.onAfterTick()
        world.removeCreatureIf(Creature::isDead)
        damages.clear()
        deaths.clear() //todo: optimize
    }

    private fun notifyAboutEvents() {
        world.allCreatures.forEach { cr ->
            damages.forEach {
                if (cr.zoneCreatures.contains(it.victim.id)) {
                    tickOuts.add(it.toUserOp(cr.id))
                }
            }
            deaths.forEach {
                if (cr.zoneCreatures.contains(it.victim.id)) {
                    tickOuts.add(it.toUserOp(cr.id))
                }
            }
        }
    }


    private fun checkPortals(cr: Creature) {
        if (cr.isPlayer) {
            for (portal in world.portals) {
                if (portal.x == cr.x && portal.y == cr.y) {
                    strategies.add(TeleportOutStrategy(tickId, this, cr, portal))
                }
            }
        }
    }

    private fun onDamage(dmg: Damage) {
        dmg.victim.damage(dmg)
        if (dmg.victim.isDead) {
            val death = Death(0, tickId, dmg.spell, dmg.victim)
            LOG.info(death.toString())
            deaths.add(death)
            movements.interrupt(dmg.victim)

            if (dmg.victim.isPlayer) {
                playersRespawns.add(RespawnPlayerStrategy(tickId, world, (dmg.victim.avatar as Player)))
            }

            dmg.spell.source.onKill(death)
        }
    }


    fun removeAvatar(userId: Int) {
        val cr = world.getCreature(userId) ?: return

        //todo allow finish step
        movements.interrupt(cr)
        world.removeCreature(cr.id)
    }

    companion object {
        private val LOG: Logger = Logger.get(Game::class.java)
    }
}

