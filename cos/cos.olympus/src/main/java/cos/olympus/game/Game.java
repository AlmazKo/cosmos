package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Death;
import cos.olympus.game.events.Fireball;
import cos.ops.AnyOp;
import cos.ops.Appear;
import cos.ops.Disconnect;
import cos.ops.Exit;
import cos.ops.FireballEmmit;
import cos.ops.FireballMoved;
import cos.ops.Login;
import cos.ops.MeleeAttack;
import cos.ops.MeleeAttacked;
import cos.ops.Move;
import cos.ops.Op;
import cos.ops.OutOp;
import cos.ops.StopMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class Game {
    private final static Logger logger = new Logger(Game.class);

    private final World                            world;
    private final DoubleBuffer<AnyOp>              bufferOps;
    private final Movements                        movements;
    private final HashMap<Integer, Player>         users           = new HashMap<>();
    private final ArrayList<RespawnStrategy>       npcRespawns     = new ArrayList<>();
    private final ArrayList<RespawnClientStrategy> playersRespawns = new ArrayList<>();
    private final ArrayList<SpellStrategy>         spells          = new ArrayList<>();
//    private final        HashMap<Integer, Creature> creatures = new HashMap<>();

    private final ArrayList<OutOp> outOps = new ArrayList<>();
    private final Zone             zone;

    int id = 0;
    private int tick = 0;

    public Game(World world, DoubleBuffer<AnyOp> bufferOps) {
        this.world = world;
        this.zone = new Zone(world);
        this.bufferOps = bufferOps;
        this.movements = new Movements(world);

        settleMobs(5);
    }

    private void settleMobs(int amount) {
        for (int i = 0; i < amount; i++) {
            npcRespawns.add(new RespawnStrategy(world, movements, CreatureType.NPC));
        }
    }

    public List<OutOp> onTick(int id, long tsm) {
        tick = id;
        outOps.clear();
        var ops = bufferOps.getAndSwap();
        onTick(id, tsm, ops);
        return outOps;
    }

    private void onTick(int id, long tsm, List<AnyOp> ops) {
        playersRespawns.removeIf(it -> it.onTick(tick, outOps));

        ops.forEach(this::handleIncomeOp);
        movements.onTick(id, tsm);
        var damages = new ArrayList<Damage>();
        var deaths = new ArrayList<Death>();
        spells.forEach(s -> s.onTick(tick, outOps, damages));

        damages.forEach(d -> {
            d.victim().damage(d);
            if (d.victim().isDead()) {
                var death = new Death(0, tick, d.spell(), d.victim());
                logger.info(death.toString());
                deaths.add(death);
                movements.interrupt(d.victim());

                if (d.victim().avatar instanceof Player) {
                    this.playersRespawns.add(new RespawnClientStrategy(tick, world, (Player) d.victim().avatar));
                }
            }
        });

        spells.forEach((SpellStrategy strategy) -> {
            var spell = strategy.spell();
            world.getAllCreatures().forEach(cr -> {
                if (strategy.inZone(cr)) {
                    if (cr.zoneSpells.put(strategy.id(), strategy) == null) {
                        if (spell instanceof Fireball s) {
                            outOps.add(new FireballMoved(id, tick, cr.id(), s.id(), s.x(), s.y(), s.speed(), s.dir(), strategy.isFinish()));
                        } else if (spell instanceof cos.olympus.game.events.MeleeAttack s) {
                            outOps.add(new MeleeAttacked(id, tick, cr.id(), s.id(), s.source().id()));
                        }
                    }
                }
            });
        });

        npcRespawns.forEach(it -> it.onTick(tick, outOps));

        world.getAllCreatures().forEach(cr -> {
            zone.onTick(cr, tick, outOps);
        });

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


        spells.removeIf(SpellStrategy::isFinish);
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
        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new Player(op.userId(), "user:" + op.userId());
            var creature = world.createCreature(usr, 100, 4);
            outOps.add(new Appear(op.id(), tick, usr.id, creature.x, creature.y, creature.mv, creature.sight, creature.life));
        } else {
            logger.warn("#" + tick + " " + "User already logged in " + usr);
        }
    }

    private void onMove(Move op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;

        movements.start(cr, op);
    }

    private void onSpell(FireballEmmit op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;
        var str = new FireballSpellStrategy(tick, cr, world);
        spells.add(str);
    }

    private void onMeleeAttack(MeleeAttack op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;
        var str = new MeleeAttackStrategy(tick, cr, world);
        spells.add(str);
    }

    private void onStopMove(StopMove op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;

        movements.stop(cr);
    }

    private void onExit(Exit op) {
        var cr = world.getCreature(op.userId());
        if (cr == null) return;

        //todo allow finish step
        movements.interrupt(cr);
        world.removeCreature(cr.id());
    }
}

