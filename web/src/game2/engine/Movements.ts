import { Dir, TileType } from '../constants';
import { World } from '../world/World';
import { Creature } from './Creature';
import { StatusMoving } from './Moving2';
import { Orientation } from './Orientation';

const GRASS_SPEED: speed = 400;
const WATER_SPEED: speed = 100;

interface Move {
  mv: Dir,
  sight: Dir
}

interface Mv {
  cr: Creature;
  start: DOMHighResTimeStamp;
  next?: Move;
}


export class Movements {

  private readonly data = new Map<uint, Mv>()

  constructor(readonly world: World) {
  }

  onFrame(time: DOMHighResTimeStamp) {
    this.data.forEach((m) => {

      if (!m.start) m.start = time;

      const o = m.cr.orientation;
      const sec = (time - m.start) / 1000;
      const shift = sec * o.vel / 100;

      if (shift < 1) {
        o.shift = shift;
        return;
      }

      const newX = this.nextX(o);
      const newY = this.nextY(o);
      const newTile = this.world.tileType(newX, newY);
      this.world.moveCreature(m.cr, newX, newY)

      if (m.next !== undefined && m.next.mv === null) {
        o.stop();
        this.data.delete(m.cr.id);
        return;
      }

      const nextDir = m.next ? m.next.mv : o.move;

      if (!this.world.canStep(o.x, o.y, nextDir)) {
        console.warn(`Step is blocked: ${o}`, o.move);
        o.stop();
        this.data.delete(m.cr.id);
        console.log("MOVING STOP")
        return;
      }


      if (m.next !== undefined) {
        o.sight = m.next.sight;
        o.move = m.next.mv;
        o.vel = Movements.getSpeed(newTile, o.move, o.sight);
      }


      m.start = time; //todo check overlaps
      o.shift -= 1;
      o.vel = Movements.getSpeed(newTile, o.move, o.sight);
    });
  }

  nextX(o: Orientation) {
    if (o.move === Dir.WEST) {
      return o.x - 1
    } else if (o.move === Dir.EAST) {
      return o.x + 1;
    } else {
      return o.x;
    }
  }

  nextY(o: Orientation): pos {
    if (o.move === Dir.NORTH) {
      return o.y - 1
    } else if (o.move === Dir.SOUTH) {
      return o.y + 1;
    } else {
      return o.y;
    }
  }

  onMovingChanged(cr: Creature, status: StatusMoving, dir: Dir, sight: Dir): boolean {
    const o = cr.orientation;
    const tile = this.world.tileType(o.x, o.y);


    const mv = this.data.get(cr.id);

    if (mv) {
      mv.next = {mv: dir, sight}
    } else {
      o.sight = sight;
      if (!this.world.canStep(o.x, o.y, dir)) {
        console.warn(`Step is blocked: ${o}`, dir);
        return false;
      }
      if (status !== StatusMoving.STOP) {
        o.move = dir;
        o.vel = Movements.getSpeed(tile, dir, sight);
        console.log("MOVING START", {status, dir, sight})
        this.data.set(cr.id, {cr, start: 0})
      }
    }
    return true
  }

  static getSpeed(tile: TileType, moving: Dir|null, sight: Dir): speed {
    if (tile === TileType.GRASS) return GRASS_SPEED;
    if (tile === TileType.SHALLOW) return WATER_SPEED;
    return 0;
    // return type === TileType.GRASS || type === TileType.DEF_VEL;
    //todo return logic
    // if (moving === sight) {
    //   return DEF_VEL;
    // } else if (sight % 2 === moving % 2) {
    //   return DEF_VEL * 4;
    // } else {
    //   return DEF_VEL * 1.5;
    // }
  }

}
