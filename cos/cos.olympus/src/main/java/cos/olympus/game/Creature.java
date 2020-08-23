package cos.olympus.game;

import cos.ops.Direction;
import org.jetbrains.annotations.Nullable;

import static cos.olympus.game.Movements.HALF;

final class Creature {
    final int    id;
    final String name;
    int   x;
    int   y;
    float offset = HALF;
    float speed  = 0;
    @Nullable Direction dir = null;
    Direction sight;

    public Creature(int id, String name, int x, int y, float offset, float speed, @Nullable Direction dir, Direction sight) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.speed = speed;
        this.dir = dir;
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
