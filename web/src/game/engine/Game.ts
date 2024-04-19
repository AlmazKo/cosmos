import {
    ActorHid,
    ActorMoved,
    API_MAPPER,
    Appear,
    Damage,
    Death,
    FireballMoved,
    MeleeAttacked,
    ObjAppear,
    OpMetrics,
    ProtoAppear,
    ShotMoved
} from '../api/ApiMessage';
import {FireballSpell} from '../api/FireballSpell';
import {Package} from '../api/Package';
import {ShotSpell} from '../api/ShotSpell';
import {ApiCreature} from '../api/ApiCreature';
import {Metrics} from './Metrics';
import {Audios} from '../audio/Audios';
import {Dir} from '../constants';
import {Api} from '../server/Api';
import {ConnStatus} from '../server/WsServer';
import {TFireball, TMelee, Trait, TShot} from '../Trait';
import {World} from '../world/World';
import {Act} from './Act';
import {OnDamage} from './actions/OnDamage';
import {OnMeleeAttack} from './actions/OnMeleeAttack';
import {ProtoArrival} from './actions/ProtoArrival';
import {Spell} from './actions/Spell';
import {Actor} from './Actor';
import {ActorObject} from './ActorObject';
import {Movements} from './Movements';
import {MovingController} from './MovingController';
import {StatusMoving} from './Moving2';
import {MovingListener} from './MovingListener';
import {Orientation} from './Orientation';
import {Player} from './Player';
import {Spells} from './Spells';
import {Chat} from './Chat';

const NO_ACTIONS: Act[] = [];
let ID = 1;

export class Game implements MovingListener {
    // @ts-ignore
    private proto: Player;
    public protoReal?: Orientation;
    private actions: Act[] = NO_ACTIONS;
    private movements: Movements;
    private chat: Chat;
    private serverTime: tsm = 0;
    private serverLatency: tsm = 0;

    constructor(
        private readonly api: Api,
        readonly world: World,
        private readonly mvg: MovingController,
        private readonly spells: Spells,
        private readonly audio: Audios,
    ) {
        this.movements = new Movements(world, this)
        api.listen(p => this.onData(p))
        mvg.listen(this)
        this.chat = new Chat();
    }

    getConnectionStatus(): ConnStatus {
        return this.api.status;
    }

    getProto(): Player | undefined {
        return this.proto;
    }

    private onData(pkg: Package) {
        this.serverTime = pkg.tickTimeMs;
        this.serverLatency = Date.now() - pkg.tickTimeMs;
        /// console.debug("Server latency:", this.serverLatency, 'Server time(ms):', (this.serverTime % 1000))
        pkg.ops.forEach(msg => {
            const action = API_MAPPER[msg.action](msg);
            console.log("%c⬇︎" + msg.action, 'color:red', action);
            switch (msg.action) {
                case 'proto_appear':
                    return this.onProtoAppear(action)
                case 'appear':
                    return this.onAppear(action)
                case 'obj_appear':
                    return this.onObjectAppear(action);
                case 'metrics':
                    return this.onMetrics(action);
                case 'actor_hid':
                    return this.onCreatureHid(action);
                case 'damage':
                    return this.onDamage(action);
                case 'death':
                    return this.onDeath(action)
                case 'fireball_moved':
                    return this.onFireballMoved(action)
                case 'shot_moved':
                    return this.onShotMoved(action)
                case 'melee_attacked':
                    return this.onMeleeAttacked(action)
                case 'actor_moved':
                    return this.onActorMoved(action)
            }
        })
    }

    private onDeath(e: Death) {
        const proto = this.proto!!;

        const msgSubject = e.creatureId == proto.id ? 'You' : `<a>#${e.creatureId}</a>`;//todo fix
        const msgVictim = e.victimId == proto.id ? 'You' : `<a>#${e.victimId}</a>`;
        this.chat.post(`${msgSubject} kills ${msgVictim} ☠️`);


        proto.zoneActors.delete(e.victimId);
        //todo add effect

        // if (!victim) return;
        //
        // victim.metrics.life -= e.amount;
        // this.actions.push(new OnDamage(ID++, proto, Date.now(), e))
    }

    private onCreatureHid(e: ActorHid) {
        const proto = this.proto!!;
        this.movements.interrupt(e.creatureId)
        proto.zoneActors.delete(e.creatureId);
    }

    private onObjectAppear(e: ObjAppear) {
        const proto = this.proto!!;
        proto.zoneObjects.set(e.id, e);
    }

    private onMetrics(e: OpMetrics) {
        const proto = this.proto!!;
        if (proto.id === e.creatureId) {
            proto.update(e)
            return;
        }

        const cr = proto.zoneActors.get(e.creatureId);
        if (cr) {
            cr.update(e)
        }
    }

    onDamage(e: Damage) {
        const proto = this.proto!!;
        let victim: Actor | undefined;
        if (proto.id === e.victimId) {
            victim = proto;
        } else {
            victim = proto.zoneActors.get(e.victimId);
        }
        if (!victim) return;

        const isProto = proto.id === victim.id;
        this.actions.push(new OnDamage(ID++, proto, Date.now(), victim, e.amount, e.crit, isProto));

        const msgSubject = e.creatureId == proto.id ? 'You' : `<a>#${e.creatureId}</a>`;//todo fix
        const msgVictim = e.victimId == proto.id ? 'You' : `<a>#${e.victimId}</a>`;
        this.chat.post(`${msgSubject} hits ${msgVictim} for <span class="${e.crit ? 'damage' : 'damage'}">${e.amount}</span>`);

        // ???
        const spell = this.proto.zoneSpells.get(e.spellId);
        if (spell) {
            spell.finished = true;
            this.proto.zoneSpells.delete(e.spellId);
            this.audio.play('damage_fireball.ogg')
        }
    }

    onAction(trait: Trait) {
        const p = this.proto!!;


        const result = this.spells.onAction(this.proto!!, trait)


        if (!result) {
            //ignores
            return;
        }


        const t = result.trait;
        // const now = Date.now()

        if (t instanceof TFireball) {
            this.api.sendAction('emmit_fireball', {});
        } else if (t instanceof TMelee) {
            this.api.sendAction('melee_attack', {});
        } else if (t instanceof TShot) {
            this.api.sendAction('emmit_shot', {});
        }

        this.audio.play(trait.audio)
        this.actions.push(result)
    }

    onFrame(time: DOMHighResTimeStamp) {
        this.movements.onFrame(time, 0) // todo: hardcode
        this.spells.onFrame(time);
    }

    getActions(): Act[] {
        return this.actions.splice(0);
    }

    private addPlayer(ac: ApiCreature): Actor {
        const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
        const m = ac.metrics;
        const mm = new Metrics(m.lvl, m.exp, m.maxLife, m.life, m.name);
        const c = new Player(ac.id, mm, o);
        // this.creatures.set(c.id, c);
        return c;
    }

    private addActor(ac: ApiCreature): Actor {
        const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
        const m = ac.metrics;
        const mm = new Metrics(m.lvl, m.exp, m.maxLife, m.life, m.name);
        const c = new ActorObject(ac.id, mm, o);
        // this.creatures.set(c.id, c);
        return c;
    }

    onMovingChanged(status: StatusMoving, dir: Dir | null, sight: Dir) {
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

    private onProtoAppear(e: ProtoAppear) {
        if (!this.proto) {
            const arrival: ApiCreature = {
                id: e.userId,
                isPlayer: true,
                x: e.x,
                y: e.y,
                sight: e.sight,
                direction: null,
                metrics: new Metrics(1, -1, 100, 100, "Player#" + e.userId),
                viewDistance: 10
            };
            this.world.set(e.world);
            this.proto = this.addPlayer(arrival) as Player;
            this.actions.push(new ProtoArrival(ID++, this.proto, Date.now()))
        } else {
            this.world.set(e.world);
            this.proto.metrics.life = this.proto.metrics.maxLife;
            this.proto.orientation.x = e.x;
            this.proto.orientation.y = e.y;
        }
    }

    private onAppear(e: Appear) {
        if (!this.proto) {
            const arrival: ApiCreature = {
                id: e.userId,
                isPlayer: true,
                x: e.x,
                y: e.y,
                sight: e.sight,
                direction: e.mv,
                metrics: new Metrics(e.lvl, -1, e.life, e.life, "Player#" + e.userId),
                viewDistance: 10
            };
            console.log("World", e.map)
            this.world.name = e.map;
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

    private onShotMoved(e: ShotMoved) {
        const proto = this.proto!!;
        if (e.finished) {
            proto.zoneSpells.delete(e.spellId);
        } else {
            if (proto.zoneSpells.has(e.spellId)) return;

            const spell = new ShotSpell(Date.now(), ID++, proto, e.speed, e.x, e.y, e.dir);
            this.actions.push(new Spell(ID++, proto, Date.now(), spell))
            proto.zoneSpells.set(e.spellId, spell);
        }
    }

    private onMeleeAttacked(e: MeleeAttacked) {
        const proto = this.proto!!;
        if (e.sourceId === proto.id) return;

        const source = proto.zoneActors.get(e.sourceId);
        if (!source) return;

        this.actions.push(new OnMeleeAttack(ID++, source, Date.now()))
    }

    private onActorMoved(e: ActorMoved) {
        const proto = this.proto!!;
        let actor: Actor | undefined;
        if (e.actorId == proto.id) {
            actor = this.proto;
            this.protoReal = new Orientation(e.mv, e.sight, e.speed, e.offset / 100, e.x, e.y);//shift hardcoded

            // fixme: now we ignore the server orientation
            // const stop = this.movements.on(cr, e.x, e.y, e.speed, e.offset, e.mv, e.sight);
            // if (stop) {
            //     this.api.sendAction('stop_move', {sight: e.sight, x: e.x, y: e.y});
            // }
        } else {
            actor = proto.zoneActors.get(e.actorId);
            if (!actor) {
                const crr: ApiCreature = {
                    id: e.actorId,
                    isPlayer: true,
                    x: e.x,
                    y: e.y,
                    sight: e.sight,
                    direction: e.mv,
                    metrics: new Metrics(-1, -1, 100, 100, "#" + e.actorId),
                    viewDistance: 10
                };
                actor = this.addActor(crr);
                proto.zoneActors.set(e.actorId, actor);
            }
            const stop = this.movements.on(actor, e.x, e.y, e.speed, e.offset, e.mv, e.sight);
        }

        // TODO
        // this.movements.onMovingChanged(cr, StatusMoving.START, e.mv, e.sight)
        // this.actions.push(new StartMoving(ID++, cr, Date.now(), 400, e.mv))
    }
}
