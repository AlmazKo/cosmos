package cos.olympus.game;

import cos.map.TileType;
import cos.ops.Direction;
import cos.ops.Move;
import cos.ops.StopMove;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cos.ops.Direction.EAST;
import static cos.ops.Direction.NORTH;
import static cos.ops.Direction.SOUTH;
import static cos.ops.Direction.WEST;
import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovementsTest {

    /*
     ...
     .≈.
     ...
     */
    private final  TileMap lakeMap     = (x, y) -> {
        if (x == 0 && y == 0) {
            return TileType.SHALLOW;
        } else if (abs(x) <= 1 && abs(y) <= 1) {
            return TileType.GRASS;
        } else {
            return TileType.NOTHING;
        }
    };
    private final  TileMap infinityMap = (x, y) -> TileType.GRASS;
    private static int     idOp        = 0;
    private static int     idTick      = 0;

    @Test
    @DisplayName("Infinity moving")
    void infinityMoving() {
        //given
        var mv = new Movements(infinityMap);
        var cr = new Creature(123, "Richard", 1, 1, SOUTH);
        mv.start(cr, mv(cr, NORTH));

        //init state
        assertTrue(cr.speed > 0);
        assertEquals(0, cr.offset);
        assertEquals(NORTH, cr.dir);
        assertEquals(NORTH, cr.sight);


        //state after 1st tick
        tick(mv); // .4
        assertEquals(cr.speed, cr.offset);
        assertEquals(NORTH, cr.dir);
        assertEquals(NORTH, cr.sight);
        assertEquals(1, cr.x);
        assertEquals(1, cr.y);

        tick(mv);// .8
        assertEquals(80, cr.offset);
        assertEquals(1, cr.x);
        assertEquals(1, cr.y);

        tick(mv);// 1.2
        assertEquals(20, cr.offset);
        assertEquals(1, cr.x);
        assertEquals(0, cr.y);

        //when
        mv.stop(cr, new StopMove(++idOp, cr.id, 1, 0, NORTH));

        tick(mv); //1.6
        assertEquals(60, cr.offset);
        assertEquals(1, cr.x);
        assertEquals(0, cr.y);

        tick(mv); //2.0
        assertEquals(0, cr.offset);
        assertEquals(0, cr.speed);
        assertEquals(1, cr.x);
        assertEquals(-1, cr.y);
    }

    @Test
    @DisplayName("Step in no-area")
    void lakeMoving() {
        //given
        var mv = new Movements(lakeMap);
        var cr = new Creature(124, "Fishman", -1, -1, SOUTH);
        mv.start(cr, mv(cr, SOUTH));
        tick(mv);// .4
        tick(mv);// .8
        tick(mv);// 1.2
        assertEquals(0, cr.y);
        tick(mv);// 1.6
        tick(mv);// 2.0
        assertEquals(1, cr.y);
        tick(mv); //2.4

        tick(mv); //2.8
        tick(mv); // 3.2
        assertEquals(-40, cr.speed);

        tick(mv); //2.8
        tick(mv); //2.4
        tick(mv); //stopped
        assertEquals(0, cr.speed);
    }

    @Test
    @DisplayName("Go through lake")
    void goThroughLake() {
        //given
        var mv = new Movements(lakeMap);
        var cr = new Creature(124, "Fishman", -1, 0, EAST);
        mv.start(cr, mv(cr, EAST));
        tick(mv);// .4
        tick(mv);// .8
        tick(mv);// 1.2
        assertEquals(20, cr.speed);
        tick(mv);// 1.4
        tick(mv);// 1.6
        tick(mv);// 1.8
        tick(mv);// 2.0
        assertEquals(40, cr.speed);
    }

    private Move mv(Creature cr, Direction dir) {
        return new Move(++idOp, cr.id, cr.x, cr.y, dir, dir);
    }

    private void tick(Movements mv) {
        mv.onTick(++idTick, 100);
    }
}
