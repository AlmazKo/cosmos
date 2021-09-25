package cos.olympus.game;

import cos.logging.Logger;
import cos.map.Coord;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Death;
import cos.olympus.util.OpConsumer;
import cos.olympus.util.OpsConsumer;
import cos.ops.AnyOp;
import cos.ops.Op;
import cos.ops.in.*;
import cos.ops.out.Disconnect;

import java.util.ArrayList;
import java.util.List;


public final class Game {
    private final static Logger logger = Logger.get(Game.class);

    private final World world;
    private final Movements movements;
    private final Spells spells;
    private final Users users;
    private final ArrayList<RespawnStrategy> npcRespawns = new ArrayList<>();
    private final ArrayList<RespawnPlayerStrategy> playersRespawns = new ArrayList<>();
    private final Zone zone;

    int id = 0;
    private int tick = 0;
    private OpConsumer outOps = new OpsConsumer();

    private Boolean settleMobs = true;

    public Game(World world) {
        this.world = world;
        this.spells = new Spells(world);
        this.users = new Users(world);
        this.zone = new Zone(world);
        this.movements = new Movements(world);

        if (settleMobs) settleMobs();
    }

    private void settleMobs() {
        world.respawns.forEach(resp -> {
            for (int i = 0; i < resp.size(); i++) {
                npcRespawns.add(new RespawnStrategy(world, spells, movements, new Coord(resp.x(), resp.y())));
            }
        });
    }

    public void onTick(int tickId, List<AnyOp> in, OpConsumer out) {
        tick = tickId;
        outOps = out;

        playersRespawns.removeIf(it -> it.onTick(tick, out));
        in.forEach(this::handleIncomeOp);
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
                    this.playersRespawns.add(new RespawnPlayerStrategy(tick, world, (Player) d.victim().avatar));
                }
            }
        });


        npcRespawns.forEach(it -> it.onTick(tick));
        world.getAllCreatures().forEach(cr -> zone.onTick(cr, tick, out));

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

    private void handleIncomeOp(AnyOp op) {
        logger.debug(">> #" + tick + " " + op.toString());
        try {
            switch (op.code()) {
                case Op.LOGIN -> onLogin((Login) op);
                case Op.MOVE -> onMove((Move) op);
                case Op.STOP_MOVE -> onStopMove((StopMove) op);
                case Op.EXIT -> onExit((ForcedExit) op);
                case Op.EMMIT_FIREBALL -> onSpell((FireballEmmit) op);
                case Op.EMMIT_SHOT -> onShot((ShotEmmit) op);
                case Op.MELEE_ATTACK -> onMeleeAttack((MeleeAttack) op);
            }
        } catch (Exception ex) {
            logger.warn("Error during processing " + op, ex);
            outOps.add(new Disconnect(op.id(), tick, op.userId()));
        }
    }

    private void onLogin(Login op) {
        var out = users.onLogin(tick, op);
        if (out != null) outOps.add(out);
    }

    private void onMove(Move op) {
        this.movements.onMove(op);
    }

    private void onShot(ShotEmmit op) {
        this.spells.onShot(tick, op);
    }

    private void onSpell(FireballEmmit op) {
        this.spells.onSpell(tick, op);
    }

    private void onMeleeAttack(MeleeAttack op) {
        this.spells.onMeleeAttack(tick, op);
    }

    private void onStopMove(StopMove op) {
        this.movements.onStopMove(op);
    }

    private void onExit(ForcedExit op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;

        //todo allow finish step
        movements.interrupt(cr);
        world.removeCreature(cr.id());
    }
}

