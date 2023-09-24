package cos.olympus.game;

import cos.logging.Logger;
import cos.map.Coord;
import cos.map.PortalSpot;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Death;
import cos.olympus.game.strategy.RespawnPlayerStrategy;
import cos.olympus.game.strategy.RespawnStrategy;
import cos.olympus.game.strategy.Strategy;
import cos.olympus.game.strategy.TeleportOutStrategy;
import cos.olympus.util.OpConsumer;
import cos.olympus.util.OpsAggregator;
import cos.ops.UserOp;
import cos.ops.in.FireballEmmit;
import cos.ops.in.Logout;
import cos.ops.in.MeleeAttack;
import cos.ops.in.Move;
import cos.ops.in.ShotEmmit;
import cos.ops.in.StopMove;
import cos.ops.out.Disconnect;

import java.util.ArrayList;
import java.util.List;

import static cos.map.CreatureType.PLAYER;


public final class Game {
    record Config(
            Boolean settleMobs
    ) {

    }

    private final static Logger logger = Logger.get(Game.class);

    private final World world;
    private final Config cfg = new Config(true);

    private final Movements movements;
    private final Spells spells;
    private final List<RespawnStrategy> npcRespawns = new ArrayList<>();
    private final List<RespawnPlayerStrategy> playersRespawns = new ArrayList<>();
    private final List<Strategy> strategies = new ArrayList<>();
    private final Damages damages = new Damages();
    private final ArrayList<Death> deaths = new ArrayList<>();//todo: channel
    private final Zone zone;
    private OpConsumer tickOuts = new OpsAggregator();

    private int tickId = 0;

    public Game(World world) {
        this.world = world;
        this.spells = new Spells(world);
        this.zone = new Zone(world);
        this.movements = new Movements(world);
        if (cfg.settleMobs()) settleMobs();
    }

    public World getWorld() {
        return world;
    }

    private void settleMobs() {
        world.respawns.forEach(resp -> {
            for (int i = 0; i < resp.size(); i++) {
                npcRespawns.add(new RespawnStrategy(world, spells, movements, new Coord(resp.x(), resp.y()), resp.type()));
            }
        });
    }

    public void handleIncomeOp(UserOp op) {
        logger.info(op, "game_in");

        try {
            switch (op) {
                case Logout o -> removeAvatar(o.userId());
                case Move o -> movements.onMove(o);
                case StopMove o -> movements.onStopMove(o);
                case FireballEmmit o -> spells.onSpell(tickId, o);
                case ShotEmmit o -> spells.onShot(tickId, o);
                case MeleeAttack o -> spells.onMeleeAttack(tickId, o);
                default -> throw new IllegalStateException("Unexpected user op: " + op);
            }
        } catch (Exception ex) {
            logger.warn("Error during processing " + op, ex);
            tickOuts.add(new Disconnect(op.id(), tickId, op.userId()));
        }
    }

    public void onTick(int tick, OpConsumer out) {
        tickId = tick;
        tickOuts = out;

        strategies.removeIf(it -> it.onTick(tick, out));
        playersRespawns.removeIf(it -> it.onTick(tick, out));
        movements.onTick(tick);
        damages.onTick(tick);

        spells.onTick(tick, damages, out);
        damages.forEach(this::onDamage);
        npcRespawns.forEach(it -> it.onTick(tick, out));
        world.getAllCreatures().forEach(cr -> zone.onTick(cr, tick, out));
        world.getAllCreatures().forEach(this::checkPortals);
        notifyAboutEvents();

        spells.onAfterTick();
        world.removeCreatureIf(Creature::isDead);
        damages.clear();
        deaths.clear();//todo: optimize
    }

    private void notifyAboutEvents() {
        world.getAllCreatures().forEach(cr -> {
            damages.forEach(d -> {
                if (cr.zoneCreatures.containsKey(d.victim().id())) {
                    tickOuts.add(d.toUserOp(cr.id()));
                }
            });

            deaths.forEach(death -> {
                if (cr.zoneCreatures.containsKey(death.victim().id())) {
                    tickOuts.add(death.toUserOp(cr.id()));
                }
            });
        });
    }

    private void checkPortals(Creature cr) {
        if (cr.is(PLAYER)) {
            for (PortalSpot portal : world.portals) {
                if (portal.x() == cr.x && portal.y() == cr.y) {
                    strategies.add(new TeleportOutStrategy(tickId, this, cr, portal));
                }
            }
        }
    }

    void onDamage(Damage dmg) {
        dmg.victim().damage(dmg);
        if (dmg.victim().isDead()) {
            var death = new Death(0, tickId, dmg.spell(), dmg.victim());
            logger.info(death.toString());
            deaths.add(death);
            movements.interrupt(dmg.victim());

            if (dmg.victim().is(PLAYER)) {
                playersRespawns.add(new RespawnPlayerStrategy(tickId, world, (Player) dmg.victim().avatar));
            }

            dmg.spell().source().onKill(death);
        }
    }

    public void placeAvatar(int userId) {

    }

    public void removeAvatar(int userId) {
        var cr = world.getCreature(userId);
        if (cr == null) return;

        //todo allow finish step
        movements.interrupt(cr);
        world.removeCreature(cr.id());
    }
}

