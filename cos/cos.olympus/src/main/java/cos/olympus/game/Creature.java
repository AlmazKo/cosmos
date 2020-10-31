package cos.olympus.game;

import cos.ops.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static cos.olympus.game.Movements.METER;
import static cos.ops.Direction.EAST;
import static cos.ops.Direction.NORTH;
import static cos.ops.Direction.SOUTH;
import static cos.ops.Direction.WEST;

final class Creature {
    final int    id;
    final String name;

    int x;
    int y;
    int offset = 0;
    int speed  = 0;
    @Nullable Direction mv = null;

    Direction         sight;
    Map<Integer, Obj> zoneObjects = new HashMap<>();

    public Creature(int id, String name, int x, int y, int offset, int speed, @Nullable Direction dir, Direction sight) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.speed = speed;
        this.mv = dir;
        this.sight = sight;
    }

    public Creature(int id, String name, int x, int y, Direction sight) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.sight = sight;
    }


    @Override public String toString() {
        return "Creature{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", rx=" + rx() +
                ", ry=" + ry() +
                ", offset=" + offset +
                ", speed=" + speed +
                ", dir=" + mv +
                ", sight=" + sight +
                '}';
    }

    public float ry() {
        if (mv == NORTH) return y - ((float) offset / METER);
        if (mv == SOUTH) return y + ((float) offset / METER);
        return y;
    }

    public float rx() {
        if (mv == WEST) return x - ((float) offset / METER);
        if (mv == EAST) return x + ((float) offset / METER);
        return x;
    }

    public void stop() {
        offset = 0;
        speed = 0;
        mv = null;
    }
}
/*) : GameObject, VectorObject {

    fun startMove(dir: Direction, sight: Direction, speed: Speed) {
        this.dir = dir
        this.sight = sight
        this.speed = speed
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return id == (other as Creature).id
    }

    override fun hashCode() = id
}*/
