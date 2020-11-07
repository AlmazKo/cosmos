import { uid } from '../../game/actions/ApiMessage';
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

  private readonly data = new Map<uid, Mv>()

  constructor(readonly world: World) {
  }

  onFrame(time: DOMHighResTimeStamp) {
    this.data.forEach((m) => {

      if (!m.start) m.start = time;

      const o = m.cr.orientation;
      const sec = (time - m.start) / 1000;
      const shift = sec * o.speed / 100;

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
        // console.log("MOVING STOP")
        return;
      }


      if (m.next !== undefined) {
        o.sight = m.next.sight;
        o.move = m.next.mv;
        o.speed = Movements.getSpeed(newTile, o.move, o.sight);
        //todo check it
        m.next = undefined;
      }


      m.start = time; //todo check overlaps
      o.shift -= 1;
      o.speed = Movements.getSpeed(newTile, o.move, o.sight);
    });
  }

  private nextX(o: Orientation) {
    if (o.move === Dir.WEST) {
      return o.x - 1
    } else if (o.move === Dir.EAST) {
      return o.x + 1;
    } else {
      return o.x;
    }
  }

  private nextY(o: Orientation): pos {
    if (o.move === Dir.NORTH) {
      return o.y - 1
    } else if (o.move === Dir.SOUTH) {
      return o.y + 1;
    } else {
      return o.y;
    }
  }


  interrupt(creatureId: uid) {
    this.data.delete(creatureId);
  }

  on(cr: Creature, x: pos, y: pos, speed: speed, move: Dir | null, sight: Dir) {
    const o = cr.orientation;
    if (speed == 0) {
      o.stop();
      o.sight = sight;

      this.data.delete(cr.id);
      this.world.moveCreature(cr, x, y);

    } else {
      this.data.delete(cr.id);
      o.x = x;
      o.y = y;
      o.sight = sight;
      o.speed = speed * 10;//fixme, it depends on server
      o.move = move;
      this.data.set(cr.id, {cr, start: 0});//fixme date
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
      if (dir === null) return false;

      if (!this.world.canStep(o.x, o.y, dir)) {
        console.warn(`Step is blocked: ${o}`, dir);
        return false;
      }
      if (status !== StatusMoving.STOP) {
        o.move = dir;
        o.speed = Movements.getSpeed(tile, dir, sight);
        // console.log("MOVING START", {status, dir, sight})
        this.data.set(cr.id, {cr, start: 0})
      }
    }
    return true
  }

  static getSpeed(tile: TileType, moving: Dir | null, sight: Dir): speed {
    if (tile === TileType.GRASS) return GRASS_SPEED;
    if (tile === TileType.SHALLOW) return WATER_SPEED;
    if (tile === TileType.GATE) return WATER_SPEED;
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
