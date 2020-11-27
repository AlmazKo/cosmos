package cos.olympus.game;

import cos.logging.Logger;
import cos.map.Coord;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Death;
import cos.olympus.util.DoubleBuffer;
import cos.ops.AnyOp;
import cos.ops.Disconnect;
import cos.ops.Exit;
import cos.ops.FireballEmmit;
import cos.ops.Login;
import cos.ops.MeleeAttack;
import cos.ops.Move;
import cos.ops.Op;
import cos.ops.OutOp;
import cos.ops.StopMove;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public final class Game {
    private final static Logger logger = new Logger(Game.class);

    private final World                            world;
    private final DoubleBuffer<AnyOp>              bufferOps;
    private final Movements                        movements;
    private final Spells                           spells;
    private final Users                            users;
    private final ArrayList<RespawnStrategy>       npcRespawns     = new ArrayList<>();
    private final ArrayList<RespawnPlayerStrategy> playersRespawns = new ArrayList<>();
    private final ArrayList<@NotNull OutOp>        outOps          = new ArrayList<>();
    private final Zone                      zone;

    int id = 0;
    private int tick = 0;

    public Game(World world, DoubleBuffer<AnyOp> bufferOps) {
        this.world = world;
        this.spells = new Spells(world);
        this.users = new Users(world);
        this.zone = new Zone(world);
        this.bufferOps = bufferOps;
        this.movements = new Movements(world);

        settleMobs(10);
    }

    private void settleMobs(int amount) {
        for (int i = 0; i < amount; i++) {
            npcRespawns.add(new RespawnStrategy(world, movements, new Coord(-26, -6)));
        }
    }

    public List<OutOp> onTick(int id, long tsm) {
        tick = id;
        outOps.clear();
        var ops = bufferOps.getAndSwap();
        onTick(ops);
        return outOps;
    }

    private void onTick(List<AnyOp> ops) {
        playersRespawns.removeIf(it -> it.onTick(tick, outOps));
        ops.forEach(this::handleIncomeOp);
        movements.onTick(tick);
        var damages = new ArrayList<Damage>();
        var deaths = new ArrayList<Death>();
        spells.onTick(tick, damages, outOps);

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
        world.getAllCreatures().forEach(cr -> zone.onTick(cr, tick, outOps));

        world.getAllCreatures().forEach(cr -> {
            damages.forEach(d -> {
                if (cr.zoneCreatures.containsKey(d.victim().id())) {
                    outOps.add(d.toUserOp(cr.id()));
                }
            });

            deaths.forEach(death -> {
                if (cr.zoneCreatures.containsKey(death.victim().id())) {
                    outOps.add(death.toUserOp(cr.id()));
                }
            });
        });

        spells.onAfterTick();
        world.removeCreatureIf(Creature::isDead);
    }

    private void handleIncomeOp(AnyOp op) {
        logger.info(">> #" + tick + " " + op.toString());
        try {
            switch (op.code()) {
                case Op.LOGIN -> onLogin((Login) op);
                case Op.MOVE -> onMove((Move) op);
                case Op.STOP_MOVE -> onStopMove((StopMove) op);
                case Op.EXIT -> onExit((Exit) op);
                case Op.EMMIT_FIREBALL -> onSpell((FireballEmmit) op);
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

    private void onSpell(FireballEmmit op) {
        this.spells.onSpell(tick, op);
    }

    private void onMeleeAttack(MeleeAttack op) {
        this.spells.onMeleeAttack(tick, op);
    }

    private void onStopMove(StopMove op) {
        this.movements.onStopMove(op);
    }

    private void onExit(Exit op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;

        //todo allow finish step
        movements.interrupt(cr);
        world.removeCreature(cr.id());
    }
}

