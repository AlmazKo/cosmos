import { CreatureMoved, ObjAppear } from '../../game/actions/ApiMessage';
import { Package } from '../../game/actions/Package';
import { ApiCreature } from '../../game/api/ApiCreature';
import { Metrics } from '../../game/Metrics';
import { Dir } from '../constants';
import { Api } from '../server/Api';
import { World } from '../world/World';
import { Act } from './Act';
import { ProtoArrival } from './actions/ProtoArrival';
import { Creature } from './Creature';
import { CreatureObject } from './CreatureObject';
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

    // console.log("onData", pkg.messages);
    // if (p.tick > this.lastTick) {
    //
    // }

    if (!this.proto) {
      const dto = pkg.messages[0].data as any;

      const arrival: ApiCreature = {
        id          : dto.id,
        isPlayer    : true,
        x           : dto.x,
        y           : dto.y,
        sight       : dto.sight,
        direction   : dto.mv,
        metrics     : new Metrics(50, 50, "Player#" + dto.userId),
        viewDistance: 10
      };

      this.proto = this.addPlayer(arrival) as Player;
      this.actions.push(new ProtoArrival(ID++, this.proto, Date.now()))
    }

    const proto = this.proto!!;

    pkg.messages.forEach(msg => {

      let e;
      console.log(msg.action, msg.data);
      switch (msg.action) {
        case 'appear_obj':
          e = msg.data as ObjAppear;
          proto.zoneObjects.set(e.id, e)
          break;
        case 'creature_moved':
          e = msg.data as CreatureMoved;


          const exist = proto.zoneCreatures.get(e.creatureId);
          if (exist) {
            this.movements.on(exist, e.x,e.y, e.speed, e.mv, e.sight)
            return;
          }

          const crr: ApiCreature = {
            id          : e.creatureId,
            isPlayer    : true,
            x           : e.x,
            y           : e.y,
            sight       : e.sight,
            direction   : e.mv,
            metrics     : new Metrics(10, 10, "#" + e.creatureId),
            viewDistance: 10
          };

          const cr = this.addCreature(crr);
          proto.zoneCreatures.set(e.creatureId, cr);
          this.world.moveCreature(cr, e.x, e.y);

          this.movements.on(cr, e.x,e.y, e.speed, e.mv, e.sight)

          // this.movements.onMovingChanged(cr, StatusMoving.START, e.mv, e.sight)

          // this.actions.push(new StartMoving(ID++, cr, Date.now(), 400, e.mv))
          break;
      }
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

  private addPlayer(ac: ApiCreature): Creature {
    const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
    const m = new Metrics(ac.metrics.maxLife, ac.metrics.life, ac.metrics.name);
    const c = new Player(ac.id, m, o);
    this.creatures.set(c.id, c);
    return c;
  }

  private addCreature(ac: ApiCreature): Creature {
    const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
    const m = new Metrics(ac.metrics.maxLife, ac.metrics.life, ac.metrics.name);
    const c = new CreatureObject(ac.id, m, o);
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

}
