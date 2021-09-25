package cos.olympus.game;

import org.junit.jupiter.api.Test;

import static cos.olympus.game.MapUtil.direction;
import static cos.olympus.game.Placeable.sample;
import static cos.ops.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapUtilTest {

    @Test
    void testDirection() {
        assertEquals(NORTH, direction(sample(2, 5), sample(2, 4)));
        assertEquals(SOUTH, direction(sample(2, 4), sample(2, 5)));
        assertEquals(EAST, direction(sample(3, 1), sample(5, 1)));
        assertEquals(WEST, direction(sample(3, 1), sample(1, 1)));
        assertNull(direction(sample(3, 3), sample(4, 2)));
    }
}