package cos.olympus.game;

import cos.logging.Logger;
import cos.olympus.DoubleBuffer;
import cos.olympus.game.events.Damage;
import cos.olympus.game.events.Fireball;
import cos.ops.AnyOp;
import cos.ops.Appear;
import cos.ops.Death;
import cos.ops.Disconnect;
import cos.ops.Exit;
import cos.ops.FireballEmmit;
import cos.ops.FireballMoved;
import cos.ops.Login;
import cos.ops.Move;
import cos.ops.Op;
import cos.ops.OutOp;
import cos.ops.StopMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class Game {
    private final static Logger logger = new Logger(Game.class);

    private final World                      world;
    private final DoubleBuffer<AnyOp>        bufferOps;
    private final Movements                  movements;
    private final HashMap<Integer, User>     users       = new HashMap<>();
    private final ArrayList<RespawnStrategy> npcRespawns = new ArrayList<>();
    private final ArrayList<SpellStrategy>   spells      = new ArrayList<>();
//    private final        HashMap<Integer, Creature> creatures = new HashMap<>();

    private final ArrayList<OutOp> outOps = new ArrayList<>();
    private final Zone             zone;

    int id = 0;
    private int tick = 0;

    public Game(World map, DoubleBuffer<AnyOp> bufferOps) {
        this.world = map;
        this.zone = new Zone(map);
        this.bufferOps = bufferOps;
        this.movements = new Movements(map);

        settleMobs(20);
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

        ops.forEach(this::handleIncomeOp);
        movements.onTick(id, tsm);
        var damages = new ArrayList<Damage>();
        var deaths = new ArrayList<Integer>();
        spells.removeIf(s -> s.onTick(tick, outOps, damages));


        damages.forEach(d -> {
            d.victim().damage(d);
            logger.info("Damage to "+ d.victim().id);
            if (d.victim().isDead()) {
                logger.info("Death "+ d.victim().id);
                deaths.add(d.victim().id);
            }
        });
        npcRespawns.forEach(it -> it.onTick(tick, outOps));

        world.getAllCreatures().forEach(cr -> {
            zone.onTick(cr, tick, outOps);
        });

        spells.forEach((SpellStrategy s) -> {
            var f = (FireballSpellStrategy) s;
            var spell = f.spell;

            world.getAllCreatures().forEach(cr -> {

                damages.forEach(d -> {
                    if (cr.zoneCreatures.containsKey(d.victim().id)) {
                        outOps.add(d.toOp(cr.id()));
                    }
                });

                deaths.forEach(victimId -> {
                    if (cr.zoneCreatures.containsKey(victimId)) {
                        outOps.add(new Death(0,tick, cr.id(), victimId));
                    }
                });

                if (s.inZone(cr)) {
                    if (cr.zoneSpells.put(s.id(), s) == null) {
                        outOps.add(new FireballMoved(id, tick, cr.id, 0, f.x, f.y, spell.speed(), spell.dir(), f.finished));
                    }
                }
            });
        });

        return outOps;
    }

    private void handleIncomeOp(AnyOp op) {
        logger.info(op.toString());
        try {
            switch (op.code()) {
                case Op.LOGIN -> onLogin((Login) op);
                case Op.MOVE -> onMove((Move) op);
                case Op.STOP_MOVE -> onStopMove((StopMove) op);
                case Op.EXIT -> onExit((Exit) op);
                case Op.EMMIT_FIREBALL -> onSpell((FireballEmmit) op);
            }
        } catch (Exception ex) {
            logger.warn("Error during processing " + op, ex);
            outOps.add(new Disconnect(op.id(), tick, op.userId()));
        }
    }

    private void onLogin(Login op) {
        var usr = users.get(op.userId());
        if (usr == null) {
            usr = new User(op.userId(), "user:" + op.userId());
            var creature = world.createCreature(usr);
            logger.info("Placed " + creature);
            outOps.add(new Appear(op.id(), tick, usr.id, creature.x, creature.y, creature.mv, creature.sight));
        } else {
            logger.warn("User already logged in " + usr);
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
        var str = new FireballSpellStrategy(Fireball.of(cr, tick), world);
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
        world.removeCreature(cr.id);
    }
}

