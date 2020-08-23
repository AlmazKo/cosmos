package cos.olympus.game;

import cos.map.TileType;
import cos.ops.Move;
import cos.ops.StopMove;
import org.junit.jupiter.api.Test;

import static cos.olympus.game.Movements.HALF;
import static cos.ops.Direction.NORTH;
import static cos.ops.Direction.SOUTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovementsTest {

    private final static float   D           = 0.0001f;
    private final        TileMap infinityMap = (x, y) -> TileType.GRASS;

    @Test
    void start() {
        var mv = new Movements(infinityMap);
        var cr = new Creature(123, "Player#123", 1, 1, SOUTH);
        mv.start(cr, new Move(1234, cr.id, 1, 1, NORTH, NORTH));

        assertTrue(cr.speed > 0);
        assertEquals(0.5f, cr.offset);
        assertEquals(NORTH, cr.dir);
        assertEquals(NORTH, cr.sight);


        mv.onTick(1, 100);
        assertEquals(HALF + cr.speed, cr.offset, D);
        assertEquals(NORTH, cr.dir);
        assertEquals(NORTH, cr.sight);
        assertEquals(1, cr.x);
        assertEquals(1, cr.y);


        mv.onTick(2, 100);
        assertEquals(0.3, cr.offset, D);
        assertEquals(1, cr.x);
        assertEquals(0, cr.y);


        mv.onTick(3, 100);
        assertEquals(0.7, cr.offset, D);
        mv.stop(cr, new StopMove(1235, cr.id, 1, 0, NORTH));


        mv.onTick(4, 100);
        assertEquals(0.1, cr.offset, D);
        assertEquals(1, cr.x);
        assertEquals(-1, cr.y);


        mv.onTick(5, 100);
        assertEquals(HALF, cr.offset, D);
        assertEquals(0, cr.speed);
        assertEquals(1, cr.x);
        assertEquals(-1, cr.y);

    }
}
