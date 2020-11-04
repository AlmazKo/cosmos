import { ObjAppear } from '../../game/actions/ApiMessage';
import { Package } from '../../game/actions/Package';
import { ApiCreature } from '../../game/api/ApiCreature';
import { Metrics } from '../../game/Metrics';
import { Dir, NO, TileType } from '../constants';
import { Api } from '../server/Api';
import { World } from '../world/World';
import { Act } from './Act';
import { ProtoArrival } from './actions/ProtoArrival';
import { Creature } from './Creature';
import { Movements } from './Movements';
import { Moving } from './Moving';
import { StatusMoving } from './Moving2';
import { MovingListener } from './MovingListener';
import { Orientation } from './Orientation';
import { Player } from './Player';

const NO_ACTIONS: Act[] = [];
let ID = 1;
const DEF_VEL: velocity = 250;


export class Game implements MovingListener {

  private lastTick = 0;
  // @ts-ignore
  private proto: Player;
  private creatures = new Map<uint, Creature>();
  private actions: Act[] = NO_ACTIONS;
  // @ts-ignore
  private movements: Movements;

  constructor(
    private readonly api: Api,
    readonly world: World,
    private readonly mvg: Moving,
  ) {
    this.movements = new Movements(world)
    api.listen(p => this.onData(p))
    mvg.listen(this)
  }


  getProto(): Player | undefined {
    return this.proto;
  }

  private onData(pkg: Package) {

    console.log("onData", pkg);
    // if (p.tick > this.lastTick) {
    //
    // }

    if (!this.proto) {
      const dto = pkg.messages[0].data as any;

      const arrival: ApiCreature = {
        id          : 4,
        isPlayer    : true,
        x           : dto.x,
        y           : dto.y,
        sight       : dto.sight,
        direction   : dto.mv,
        metrics     : new Metrics(50, 50, "Player#3"),
        viewDistance: 10
      };

      this.proto = this.addCreature(arrival);
      this.actions.push(new ProtoArrival(ID++, this.proto, Date.now()))
    }

    pkg.messages.forEach(msg => {

      if (msg.action === 'appear_obj') {
        //{id; x: 11, y: 5, tileId: 232}
        const p = this.proto!!;
        const e = msg.data as ObjAppear;
        p.zoneObjects.set(e.id, e)
      }
      // actions.push()msg.action
    })

  }


  onFrame(time: DOMHighResTimeStamp) {
    this.movements.onFrame(time)
  }


  getActions(): Act[] {
    return this.actions.splice(0);
  }

  // request(req: Request): boolean {
  //
  // }
  //
  //
  // getVision(): Vision {
  //
  // }

  private addCreature(ac: ApiCreature): Creature {
    const o = new Orientation(NO, ac.sight, 0, 0.0, ac.x, ac.y);
    const m = new Metrics(ac.metrics.maxLife, ac.metrics.life, ac.metrics.name);
    const c = new Player(ac.id, m, o);
    this.creatures.set(c.id, c);
    return c;
  }

  onMovingChanged(status: StatusMoving, dir: Dir, sight: Dir) {

    const accepted = this.movements.onMovingChanged(this.proto!!, status, dir, sight);

    if (accepted) {
      const o = this.proto!!.orientation;
      if (status === StatusMoving.STOP) {
        this.api.sendAction('stop_move', {sight, x: o.x, y: o.y});
      } else {
        this.api.sendAction('move', {dir, sight, x: o.x, y: o.y});
      }
    }
  }


  static getVelocity(tile: TileType, moving: Dir, sight: Dir): number {
    if (tile === TileType.GRASS) return DEF_VEL;
    if (tile === TileType.SHALLOW) return DEF_VEL * 2;
    return DEF_VEL * 4;
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
