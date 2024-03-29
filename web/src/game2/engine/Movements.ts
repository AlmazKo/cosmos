import { uid } from '../../game/actions/ApiMessage';
import { Dir, TileType } from '../constants';
import { World } from '../world/World';
import { Creature } from './Creature';
import { Game } from './Game';
import { StatusMoving } from './Moving2';
import { Orientation } from './Orientation';
import { Util } from './Util';

const GRASS_SPEED: speed = 40;
const WATER_SPEED: speed = 10;

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

  constructor(readonly world: World, private readonly game: Game) {
  }

  onFrame(time: DOMHighResTimeStamp) {
    this.data.forEach((m) => {
      if (!m.start) m.start = time;

      const o = m.cr.orientation;
      const newTicks = (time - m.start) / 100;
      const newOffset = Math.round(o.offset + newTicks * o.speed);

      m.start = time;

      if (newOffset < 100) {
        o.offset = newOffset;
        o.shift = newOffset / 100;
        console.debug('C', o.offset)
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

      if (this.isBlocked(o, nextDir)) {
        console.warn(`#${m.cr.id} Step is blocked: ${o}`, o.move);
        o.stop();
        this.data.delete(m.cr.id);
        // console.log("MOVING STOP")
        return;
      }


      if (m.next !== undefined) {
        o.sight = m.next.sight;
        o.move = m.next.mv;
        o.speed = Movements.getSpeed(newTile, o.move, o.sight);
        m.next = undefined;
      }


      // m.start = time; //todo check overlaps
      o.offset -= 100;
      console.debug('C', o.offset)
      o.shift -= 1;
      //  o.speed = Movements.getSpeed(newTile, o.move, o.sight);
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

  on(cr: Creature, x: pos, y: pos, speed: speed, offset: uint, move: Dir | null, sight: Dir): boolean {
    const o = cr.orientation;


    console.debug('current', JSON.stringify(cr.orientation))
    console.debug('    new', JSON.stringify(new Orientation(move, sight, speed, offset, x, y)))

    if (speed == 0) {
      o.stop();
      o.sight = sight;

      this.data.delete(cr.id);
      this.world.moveCreature(cr, x, y);

    } else {

      const mv = this.data.get(cr.id);

      if (mv && o.x === x && o.y === y) {
        o.x = x;
        o.y = y;
        o.shift = offset / 100;
        o.offset = offset;
        console.log('S', o.offset)
        o.sight = sight;
        o.speed = speed;//fixme, it depends on server
        o.move = move;
        // this.data.set(cr.id, {cr, start: 0});//fixme date

        //patch
      } else {
        this.data.delete(cr.id);
        o.x = x;
        o.y = y;
        o.sight = sight;
        o.offset = offset;
        o.shift = offset / 100;
        o.speed = speed;//fixme, it depends on server
        o.move = move;
        console.debug('SS', o.offset);


        if (this.isBlocked(o, o.move)) {
          console.warn(`Step is blocked: ${o}`, o.move);
          return true;
        }
        this.data.set(cr.id, {cr, start: 0});//fixme date
      }
    }
    return false;
  }

  onMovingChanged(cr: Creature, status: StatusMoving, dir: Dir, sight: Dir): boolean {
    const o = cr.orientation;
    const mv = this.data.get(cr.id);

    if (mv) {
      mv.next = {mv: dir, sight}
    } else {
      o.sight = sight;
      if (dir === null) return true;

      if (this.isBlocked(o, dir)) {
        console.warn(`Step is blocked: ${o}`, dir);
        return cr.orientation.sight !== sight;
      }
      if (status !== StatusMoving.STOP) {
        o.move = dir;
        o.speed = 40;
        // console.log("MOVING START", {status, dir, sight})
        this.data.set(cr.id, {cr, start: 0})
      }
    }
    return true
  }

  static getSpeed(tile: TileType, moving: Dir | null, sight: Dir): speed {
    if (tile === TileType.GRASS) return GRASS_SPEED;
    if (tile === TileType.SAND) return GRASS_SPEED;
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

  private isBlocked(o: Orientation, dir: Dir | null): boolean {
    if (!this.world.canStep(o.x, o.y, dir)) return true;

    const nextX = Util.nextX(o, dir);
    const nextY = Util.nextY(o, dir);
    const proto = this.game.getProto()!!;

    for (let [_, v] of proto.zoneCreatures) {
      if (v.id === proto.id) continue;

      if (v.orientation.x == nextX && v.orientation.y === nextY) {
        return true;
      }
    }

    // todo
    // for (let [_, v] of proto.zoneObjects) {
    //   if (v.x == nextX && v.y === nextY) {
    //     return true;
    //   }
    // }

    return false;
  }
}
