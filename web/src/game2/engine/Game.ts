import { Package } from '../../game/actions/Package';
import { ApiArrival } from '../../game/api/ApiArrival';
import { ApiCreature } from '../../game/api/ApiCreature';
import { Metrics } from '../../game/Metrics';
import { Dir, dirToString, NO } from '../constants';
import { Api } from '../server/Api';
import { World } from '../world/World';
import { Act } from './Act';
import { ProtoArrival } from './actions/ProtoArrival';
import { Creature } from './Creature';
import { Focus, Moving } from './Moving';
import { MovingListener } from './MovingListener';
import { Orientation } from './Orientation';
import { Player } from './Player';
import { ProtoMoving } from './ProtoMoving';

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
  private protoMoving: ProtoMoving;

  constructor(
    private readonly api: Api,
    readonly world: World,
    private readonly mvg: Moving,
  ) {
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
      const arrival = pkg.messages[0].data as ApiArrival;
      this.proto = this.addCreature(arrival.creature);
      this.protoMoving = new ProtoMoving(this.proto.orientation, this)
      this.actions.push(new ProtoArrival(ID++, this.proto, Date.now()))
    }

    pkg.messages.forEach(msg => {
      // actions.push()msg.action
    })

  }


  onFrame(time: DOMHighResTimeStamp) {

    if (this.protoMoving) {
      this.protoMoving.onFrame(time)
    }
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

    //fixme
    const c = new Player(ac.id, new Metrics(10, 10, "Test1"), new Orientation(NO, Dir.SOUTH, 0, 0.0, ac.x, ac.y));
    this.creatures.set(c.id, c);
    return c;
  }

  sonStartMoving(f: Focus) {

    // this.actions.push(new StartMoving(ID++, this.proto, Date.now(), 200, f.move))

  }

  onStartMoving(moving: Dir, sight: Dir): void {
    const o = this.proto!!.orientation;
    if (!this.world.canStep(o.x, o.y, moving)) {
      console.warn(`Step is blocked: ${o}`, dirToString(moving));
      return;
    }

    this.api.sendAction({moving, sight});
    o.setMoving(moving, (!sight) ? moving : sight, DEF_VEL);
  }

  onChangeMoving(moving: Dir, sight: Dir): void {
    const o = this.proto!!.orientation;
    if (!o.move) return;
    const vel = Game.getVelocity(moving, sight);
    o.setNext(moving, (!sight) ? moving : sight, vel)
  }

  onStopMoving(moving: Dir, sight: Dir): void {
    const o = this.proto!!.orientation;
    this.api.sendAction({moving, sight});
    const vel = Game.getVelocity(moving, sight);
    o.setNext(moving, (!sight) ? moving : sight, vel)
  }

  static getVelocity(moving: Dir, sight: Dir) {
    if (moving === sight) {
      return DEF_VEL;
    } else if (sight % 2 === moving % 2) {
      return DEF_VEL * 4;
    } else {
      return DEF_VEL * 1.5;
    }
  }

}
