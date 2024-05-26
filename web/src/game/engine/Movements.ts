import {uid} from '../api/ApiMessage';
import {Dir, TileType} from '../constants';
import {World} from '../world/World';
import {Actor} from './Actor';
import {Game} from './Game';
import {StatusMoving} from './Moving2';
import {Orientation} from './Orientation';
import {Util} from './Util';

const GRASS_SPEED: speed = 40;
const WATER_SPEED: speed = 10;
const TICK: ms = 100;

type TileSpeed = {
    [key in TileType]?: number;
};

const TILE_SPEED: TileSpeed = {
    [TileType.GRASS]: 40,
    [TileType.SAND]: 40,
    [TileType.SHALLOW]: 10,
    [TileType.GATE]: 10,
}

interface Move {
    mv: Dir,
    sight: Dir
}

interface Mv {
    cr: Actor;
    start: DOMHighResTimeStamp;
    next?: Move;
}


export class Movements {

    private readonly data = new Map<uid, Mv>()

    constructor(readonly world: World, private readonly game: Game) {
    }

    onFrame(time: DOMHighResTimeStamp, serverLatency: ms) {
        this.data.forEach((mv) => {
            if (!mv.start) {
                mv.start = time;
                /// console.info('Started', time)
            } else {
                /// console.info('Updated', time)
            }


            const o = mv.cr.orientation;
            const ticksPass = (time - mv.start) / TICK;
            const newOffset = Math.round(o.offset + ticksPass * o.speed);

            mv.start = time;

            /// console.debug('newOffset', newOffset, 'New ticks',ticksPass)
            if (newOffset < TICK) {
                o.offset = newOffset;
                o.shift = newOffset / TICK;
                return;
            }

            const newX = this.nextX(o);
            const newY = this.nextY(o);
            const newTile = this.world.tileType(newX, newY);
            this.world.moveCreature(mv.cr, newX, newY)

            if (mv.next !== undefined && mv.next.mv === null) {
                o.stop();
                this.data.delete(mv.cr.id);
                return;
            }

            const nextDir = mv.next ? mv.next.mv : o.move;

            if (this.isBlocked(o, nextDir)) {
                console.warn(`#${mv.cr.id} Step is blocked: ${o}`, o.move);
                o.stop();
                this.data.delete(mv.cr.id);
                // console.log("MOVING STOP")
                return;
            }


            if (mv.next !== undefined) {
                o.sight = mv.next.sight;
                o.move = mv.next.mv;
                o.speed = Movements.getSpeed(newTile, o.move, o.sight);
                mv.next = undefined;
            }


            // m.start = time; ///todo check overlaps
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

    on(cr: Actor, x: pos, y: pos, speed: speed, offset: uint, move: Dir | null, sight: Dir): boolean {
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

    onMovingChanged(cr: Actor, status: StatusMoving, dir: Dir | null, sight: Dir): boolean {
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
                const tile = this.world.tileType(o.x, o.y);
                o.speed = Movements.getSpeed(tile, o.move, o.sight);
                // console.log("MOVING START", {status, dir, sight})
                this.data.set(cr.id, {cr, start: 0})
            }
        }
        return true
    }

    static getSpeed(tile: TileType, moving: Dir | null, sight: Dir): speed {
        return TILE_SPEED[tile] || 0;

        // return type === TileType.GRASS || type === TileType.DEF_VEL;
        // //todo return logic
        // if (moving === sight) {
        //   return baseSpeed;
        // } else if (sight % 2 === moving % 2) {
        //   return baseSpeed /4;
        // } else {
        //   return DEF_VEL * 1.5;
        // }
    }

    private isBlocked(o: Orientation, dir: Dir | null): boolean {
        if (!this.world.canStep(o.x, o.y, dir)) return true;

        const nextX = Util.nextX(o, dir);
        const nextY = Util.nextY(o, dir);
        const proto = this.game.getProto()!!;

        for (let [_, v] of proto.zoneActors) {
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
