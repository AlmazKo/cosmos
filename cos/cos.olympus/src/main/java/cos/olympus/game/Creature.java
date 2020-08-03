package cos.olympus.game;

import cos.ops.Direction;

import static cos.ops.Direction.SOUTH;
import static cos.olympus.game.Movements.HALF;

final class Creature {
    final int    id;
    final String name;
    int       x;
    int       y;
    int       offset = HALF;
    int       speed = 0;
    Direction dir   = SOUTH;
    Direction sight = SOUTH;

    public Creature(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Creature(int id, String name, int x, int y, int offset, int speed, Direction dir, Direction sight) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.speed = speed;
        this.dir = dir;
        this.sight = sight;
    }

    @Override public String toString() {
        return "Creature{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", offset=" + offset +
                ", speed=" + speed +
                ", dir=" + dir +
                ", sight=" + sight +
                '}';
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
