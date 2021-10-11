package cos.olympus.game;

import cos.map.Land;
import cos.olympus.util.OpsConsumer;
import cos.ops.out.CreatureHid;
import cos.ops.out.CreatureMoved;
import cos.ops.OutOp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoneTest {
//
//
    @Test
    @DisplayName("Update zone")
    void infinityMoving() throws IOException {
//        var lands = Land.load(Paths.get("", "../../resources").toAbsolutePath());
//        var world = new World(lands);
//        var player = world.createCreature(new  Player(200, "Player", 46, 21),100,4);
//        var log = world.createCreature(new Player(30000, "Log", 48, 21),100,4);
//        var zone = new Zone(world);
//
//        var ops = new OpsConsumer();
//        zone.onTick(player, 1, ops);
//        assertEquals(2, player.zoneCreatures.size());
//        assertEquals(4, ops.size());
//        assertEquals(ops.getUserData(200), ((CreatureMoved) ops.data.get(0)).creatureId());
//        assertEquals(0, ((CreatureMoved) ops.data.get(0)).speed());
//        assertEquals(0, ((CreatureMoved) ops.data.get(2)).speed());
//        assertEquals(log.id(), ((CreatureMoved) ops.data.get(2)).creatureId());
//
//        ops.clear();
//        world.moveCreature(log, 7, 4);
//        zone.onTick(player, 1, ops);
//        assertEquals(1, player.zoneCreatures.size());
//        assertEquals(log.id(), ((CreatureHid) ops.data.get(0)).creatureId());

//        ops.clear();
//        world.moveCreature(player, -1, 0);
//        zone.onTick(player, 1, ops);
//        assertEquals(2, player.zoneCreatures.size());
//        assertEquals(2, ops.size());
//        assertEquals(player.id(), ((CreatureMoved) ops.data.get(0)).creatureId());
//        assertEquals(log.id(), ((CreatureMoved) ops.data.get(1)).creatureId());
    }

//
//    private static int  idOp        = 0;
//    private static int  idTick      = 0;
//
//    @Test
//    @DisplayName("Infinity moving")
//    void infinityMoving() {
//        //given
//        var mv = new Movements(infinityMap);
//        var cr = infinityMap.createCreature(new User(123, "Richard", 1, 1, SOUTH));
//        mv.start(cr, mv(cr, NORTH));
//
//        //init state
//        assertTrue(cr.speed > 0);
//        assertEquals(0, cr.offset);
//        assertEquals(NORTH, cr.dir);
//        assertEquals(NORTH, cr.sight);
//
//
//        //state after 1st tick
//        tick(mv); // .4
//        assertEquals(cr.speed, cr.offset);
//        assertEquals(NORTH, cr.dir);
//        assertEquals(NORTH, cr.sight);
//        assertEquals(1, cr.x);
//        assertEquals(1, cr.y);
//
//        tick(mv);// .8
//        assertEquals(80, cr.offset);
//        assertEquals(1, cr.x);
//        assertEquals(1, cr.y);
//
//        tick(mv);// 1.2
//        assertEquals(20, cr.offset);
//        assertEquals(1, cr.x);
//        assertEquals(0, cr.y);
//
//        //when
//        mv.stop(cr, new StopMove(++idOp, cr.id, 1, 0, NORTH));
//
//        tick(mv); //1.6
//        assertEquals(60, cr.offset);
//        assertEquals(1, cr.x);
//        assertEquals(0, cr.y);
//
//        tick(mv); //2.0
//        assertEquals(0, cr.offset);
//        assertEquals(0, cr.speed);
//        assertEquals(1, cr.x);
//        assertEquals(-1, cr.y);
//    }
//
//    @Test
//    @DisplayName("Step in no-area")
//    void lakeMoving() {
//        //given
//        var mv = new Movements(lakeMap);
//        var cr = new Creature(124, "Fishman", -1, -1, SOUTH);
//        mv.start(cr, mv(cr, SOUTH));
//        tick(mv);// .4
//        tick(mv);// .8
//        tick(mv);// 1.2
//        assertEquals(0, cr.y);
//        tick(mv);// 1.6
//        tick(mv);// 2.0
//        assertEquals(1, cr.y);
//        tick(mv); //2.4
//
//        tick(mv); //2.8
//        tick(mv); // 3.2
//        assertEquals(-40, cr.speed);
//
//        tick(mv); //2.8
//        tick(mv); //2.4
//        tick(mv); //stopped
//        assertEquals(0, cr.speed);
//    }
//
//    @Test
//    @DisplayName("Go through lake")
//    void goThroughLake() {
//        //given
//        var mv = new Movements(lakeMap);
//        var cr = lakeMap.createCreature(new User(124, "Fishman"));
//        cr.mv(-1, 0);
//        cr.sight = EAST;
//        mv.start(cr, mv(cr, EAST));
//        tick(mv);// .4
//        tick(mv);// .8
//        tick(mv);// 1.2
//        assertEquals(20, cr.speed);
//        tick(mv);// 1.4
//        tick(mv);// 1.6
//        tick(mv);// 1.8
//        tick(mv);// 2.0
//        assertEquals(40, cr.speed);
//    }
//
//    private Move mv(Creature cr, Direction dir) {
//        return new Move(++idOp, cr.id, cr.x, cr.y, dir, dir);
//    }
//
//    private void tick(Movements mv) {
//        mv.onTick(++idTick, 100);
//    }
}
