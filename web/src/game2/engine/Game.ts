import { Appear, CreatureHid, CreatureMoved, Damage, Death, FireballMoved, MeleeAttacked, ObjAppear } from '../../game/actions/ApiMessage';
import { FireballSpell } from '../../game/actions/FireballSpell';
import { Package } from '../../game/actions/Package';
import { ApiCreature } from '../../game/api/ApiCreature';
import { Metrics } from '../../game/Metrics';
import { Trait, TraitFireball, TraitMelee } from '../../game/Trait';
import { Dir } from '../constants';
import { Api } from '../server/Api';
import { ConnStatus } from '../server/WsServer';
import { World } from '../world/World';
import { Act } from './Act';
import { ActivateTrait } from './actions/ActivateTrait';
import { OnDamage } from './actions/OnDamage';
import { OnMeleeAttack } from './actions/OnMeleeAttack';
import { ProtoArrival } from './actions/ProtoArrival';
import { Spell } from './actions/Spell';
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

export class Game implements MovingListener {

  private lastTick = 0;
  // @ts-ignore
  private proto: Player;
  public protoReal?: Orientation;
  // private creatures = new Map<uint, Creature>();
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

  getConnectionStatus(): ConnStatus {
    return this.api.status;
  }

  getProto(): Player | undefined {
    return this.proto;
  }

  private onData(pkg: Package) {
    pkg.messages.forEach(msg => {
      let e = {...msg.data, tickId: pkg.tick};
      console.log(msg.action, e);
      switch (msg.action) {
        case 'appear':
          this.onAppear(e)
          break;
        case 'appear_obj':
          this.onObjectAppear(e);
          break;
        case 'creature_hid':
          this.onCreatureHid(e);
          break
        case 'damage':
          this.onDamage(e);
          break;
        case 'death':
          this.onDeath(e);
          break;
        case 'fireball_moved':
          this.onFireballMoved(e)
          break
        case 'melee_attacked':
          this.onMeleeAttacked(e)
          break
        case 'creature_moved':
          this.onCreatureMove(e)
          break;
      }
    })
  }

  private onDeath(e: Death) {
    const proto = this.proto!!;
    proto.zoneCreatures.delete(e.victimId);
    //todo add effect

    // if (!victim) return;
    //
    // victim.metrics.life -= e.amount;
    // this.actions.push(new OnDamage(ID++, proto, Date.now(), e))
  }

  private onCreatureHid(e: CreatureHid) {
    const proto = this.proto!!;
    this.movements.interrupt(e.creatureId)
    proto.zoneCreatures.delete(e.creatureId);
  }

  private onObjectAppear(e: ObjAppear) {
    const proto = this.proto!!;
    proto.zoneObjects.set(e.id, e);
  }

  onDamage(e: Damage) {
    const proto = this.proto!!;
    let victim: Creature | undefined;
    if (proto.id === e.victimId) {
      victim = proto;
    } else {
      victim = proto.zoneCreatures.get(e.victimId);
    }
    if (!victim) return;

    const isProto = proto.id === victim.id;
    victim.metrics.life -= e.amount;
    this.actions.push(new OnDamage(ID++, proto, Date.now(), victim, e.amount, e.crit, isProto));

    // ???
    const spell = this.proto.zoneSpells.get(e.spellId);
    if (spell) {
      spell.finished = true;
      this.proto.zoneSpells.delete(e.spellId);
    }
  }

  onAction(trait: Trait) {
    const p = this.proto!!;
    if (trait instanceof TraitFireball) {
      this.api.sendAction('emmit_fireball', {});
    } else if (trait instanceof TraitMelee) {
      this.api.sendAction('melee_attack', {});
    }

    this.actions.push(new ActivateTrait(ID++, p, Date.now(), trait))
  }

  onFrame(time: DOMHighResTimeStamp) {
    this.movements.onFrame(time)
  }


  getActions(): Act[] {
    return this.actions.splice(0);
  }

  private addPlayer(ac: ApiCreature): Creature {
    const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
    const m = new Metrics(ac.metrics.maxLife, ac.metrics.life, ac.metrics.name);
    const c = new Player(ac.id, m, o);
    // this.creatures.set(c.id, c);
    return c;
  }

  private addCreature(ac: ApiCreature): Creature {
    const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
    const m = new Metrics(ac.metrics.maxLife, ac.metrics.life, ac.metrics.name);
    const c = new CreatureObject(ac.id, m, o);
    // this.creatures.set(c.id, c);
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

  private onAppear(e: Appear) {
    if (!this.proto) {
      const arrival: ApiCreature = {
        id          : e.userId,
        isPlayer    : true,
        x           : e.x,
        y           : e.y,
        sight       : e.sight,
        direction   : e.mv,
        metrics     : new Metrics(e.life, e.life, "Player#" + e.userId),
        viewDistance: 10
      };

      this.proto = this.addPlayer(arrival) as Player;
      this.actions.push(new ProtoArrival(ID++, this.proto, Date.now()))
    } else {
      this.proto.metrics.life = this.proto.metrics.maxLife;
      this.proto.orientation.x = e.x;
      this.proto.orientation.y = e.y;
    }
  }

  private onFireballMoved(e: FireballMoved) {
    const proto = this.proto!!;
    if (e.finished) {
      proto.zoneSpells.delete(e.spellId);
    } else {
      if (proto.zoneSpells.has(e.spellId)) return;

      const spell = new FireballSpell(Date.now(), ID++, proto, e.speed, e.x, e.y, e.dir);
      this.actions.push(new Spell(ID++, proto, Date.now(), spell))
      proto.zoneSpells.set(e.spellId, spell);
    }
  }

  private onMeleeAttacked(e: MeleeAttacked) {
    const proto = this.proto!!;
    if (e.sourceId === proto.id) return;

    const source = proto.zoneCreatures.get(e.sourceId);
    if (!source) return;

    this.actions.push(new OnMeleeAttack(ID++, source, Date.now()))
  }

  private onCreatureMove(e: CreatureMoved) {
    const proto = this.proto!!;
    let cr: Creature | undefined;
    if (e.creatureId == proto.id) {
      cr = this.proto;
      this.protoReal = new Orientation(e.mv, e.sight, e.speed, e.offset / 100, e.x, e.y);//shift hardcoded


      const stop = this.movements.on(cr, e.x, e.y, e.speed, e.offset, e.mv, e.sight);
      if (stop) {
        this.api.sendAction('stop_move', {sight: e.sight, x: e.x, y: e.y});
      }
    } else {
      cr = proto.zoneCreatures.get(e.creatureId);
      if (!cr) {
        const crr: ApiCreature = {
          id          : e.creatureId,
          isPlayer    : true,
          x           : e.x,
          y           : e.y,
          sight       : e.sight,
          direction   : e.mv,
          metrics     : new Metrics(100, 100, "#" + e.creatureId),
          viewDistance: 10
        };
        cr = this.addCreature(crr);
        proto.zoneCreatures.set(e.creatureId, cr);
      }
      const stop = this.movements.on(cr, e.x, e.y, e.speed, e.offset, e.mv, e.sight);
    }


    // cr = proto.zoneCreatures.get(e.creatureId);
    // if (cr) {
    //
    // } else {
    //
    //   proto.zoneCreatures.set(e.creatureId, this.proto);
    // }


    // proto.zoneCreatures.set(e.creatureId, cr);
    // this.movements.on(cr, e.x, e.y, e.speed, e.mv, e.sight)

    // this.movements.onMovingChanged(cr, StatusMoving.START, e.mv, e.sight)

    // this.actions.push(new StartMoving(ID++, cr, Date.now(), 400, e.mv))
  }
}
