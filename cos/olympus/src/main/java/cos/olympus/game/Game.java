package cos.olympus.game;

import cos.logging.Logger;
import cos.map.Coord;
import cos.map.CreatureType;
import cos.map.PortalSpot;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Death;
import cos.olympus.util.OpConsumer;
import cos.olympus.util.OpsConsumer;
import cos.ops.UserOp;
import cos.ops.Op;
import cos.ops.in.FireballEmmit;
import cos.ops.in.MeleeAttack;
import cos.ops.in.Move;
import cos.ops.in.ShotEmmit;
import cos.ops.in.StopMove;
import cos.ops.out.Disconnect;

import java.util.ArrayList;
import java.util.List;


public final class Game {
    private final static Logger logger = Logger.get(Game.class);

    private final World world;

    private final Movements movements;
    private final Teleports teleports;
    private final Spells spells;
    private final List<RespawnStrategy> npcRespawns = new ArrayList<>();
    private final List<RespawnPlayerStrategy> playersRespawns = new ArrayList<>();
    private final Zone zone;
    private OpConsumer tickOuts = new OpsConsumer();

    private int tickId = 0;
    private Boolean settleMobs = true;

    public Game(World world, Teleports teleports) {
        this.world = world;
        this.spells = new Spells(world);
        this.zone = new Zone(world);
        this.movements = new Movements(world);
        this.teleports = teleports;
//        npcRespawns.add(new RespawnStrategy(world, spells, movements, new Coord(5, 5), NpcType.WOLF));
        if (settleMobs) settleMobs();
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

    public void onTick(int tick, OpConsumer out) {
        tickId = tick;
        tickOuts = out;

        playersRespawns.removeIf(it -> it.onTick(tick, out));
        movements.onTick(tick);
        var damages = new ArrayList<Damage>();
        var deaths = new ArrayList<Death>();
        spells.onTick(tick, damages, out);

        damages.forEach(d -> {
            d.victim().damage(d);
            if (d.victim().isDead()) {
                var death = new Death(0, tick, d.spell(), d.victim());
                logger.info(death.toString());
                deaths.add(death);
                movements.interrupt(d.victim());

                if (d.victim().avatar instanceof Player) {
                    playersRespawns.add(new RespawnPlayerStrategy(tick, world, (Player) d.victim().avatar));
                }

                d.spell().source().onKill(death);
            }
        });


        npcRespawns.forEach(it -> it.onTick(tick));
        world.getAllCreatures().forEach(cr -> zone.onTick(cr, tick, out));
        world.getAllCreatures().forEach(cr -> {
            if (cr.type() == CreatureType.PLAYER) {
                for (PortalSpot portal : world.portals) {
                    if (portal.x() == cr.x && portal.y() == cr.y) {
                        teleports.activate(tickId, (Player) cr.avatar, portal);
                    }
                }
            }
        });


        world.getAllCreatures().forEach(cr -> {
            damages.forEach(d -> {
                if (cr.zoneCreatures.containsKey(d.victim().id())) {
                    out.add(d.toUserOp(cr.id()));
                }
            });

            deaths.forEach(death -> {
                if (cr.zoneCreatures.containsKey(death.victim().id())) {
                    out.add(death.toUserOp(cr.id()));
                }
            });
        });

        spells.onAfterTick();
        world.removeCreatureIf(Creature::isDead);
    }

    public void handleIncomeOp(UserOp op) {
        //logger.info(">> #" + tickId + " " + op.toString());
        try {
            switch (op.code()) {
                case Op.LOGOUT -> removeAvatar(op.userId());

                case Op.MOVE -> movements.onMove((Move) op);
                case Op.STOP_MOVE -> movements.onStopMove((StopMove) op);

                case Op.EMMIT_FIREBALL -> spells.onSpell(tickId, (FireballEmmit) op);
                case Op.EMMIT_SHOT -> spells.onShot(tickId, (ShotEmmit) op);
                case Op.MELEE_ATTACK -> spells.onMeleeAttack(tickId, (MeleeAttack) op);
            }
        } catch (Exception ex) {
            logger.warn("Error during processing " + op, ex);
            tickOuts.add(new Disconnect(op.id(), tickId, op.userId()));
        }
    }

    private void removeAvatar(int userId) {
        var cr = world.getCreature(userId);
        if (cr == null) return;

        //todo allow finish step
        movements.interrupt(cr);
        world.removeCreature(cr.id());
    }
}

