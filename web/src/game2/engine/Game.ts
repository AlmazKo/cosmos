import {
    Appear,
    CreatureHid,
    CreatureMoved,
    Damage,
    Death,
    FireballMoved,
    MeleeAttacked,
    ObjAppear,
    OpMetrics,
    ShotMoved
} from '../../game/actions/ApiMessage';
import {FireballSpell} from '../../game/actions/FireballSpell';
import {Package} from '../../game/actions/Package';
import {ShotSpell} from '../../game/actions/ShotSpell';
import {ApiCreature} from '../../game/api/ApiCreature';
import {Metrics} from '../../game/Metrics';
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
import {Creature} from './Creature';
import {CreatureObject} from './CreatureObject';
import {Movements} from './Movements';
import {Moving} from './Moving';
import {StatusMoving} from './Moving2';
import {MovingListener} from './MovingListener';
import {Orientation} from './Orientation';
import {Player} from './Player';
import {Spells} from './Spells';

const NO_ACTIONS: Act[] = [];
let ID = 1;
const dt = Intl.DateTimeFormat('en', {
    hour: 'numeric',
    minute: 'numeric',
    hour12: false
});


export class Game implements MovingListener {
    // @ts-ignore
    private proto: Player;
    public protoReal?: Orientation;
    private actions: Act[] = NO_ACTIONS;
    private movements: Movements;
    private chat: HTMLElement;
    private chat_in: HTMLInputElement;

    constructor(
        private readonly api: Api,
        readonly world: World,
        private readonly mvg: Moving,
        private readonly spells: Spells,
        private readonly audio: Audios,
    ) {
        this.movements = new Movements(world, this)
        api.listen(p => this.onData(p))
        mvg.listen(this)


        this.chat = document.getElementById("chat_history")!!;
        this.chat_in = document.getElementById("chat_input") as any;

        document.addEventListener("keyup", event => {
            if (event.code === "Tab") {
                event.stopPropagation();
                return false;
            }
        });
        this.chat_in.addEventListener("keyup", event => {
            console.warn(event)
            if (event.keyCode === 13) {
                this.onChatMessage(`You says ${this.chat_in.value}`);
                this.chat_in.value = '';
                event.preventDefault();
                this.chat_in.blur();
            }
        });
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
                case 'metrics':
                    this.onMetrics(e);
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
                    break;
                case 'shot_moved':
                    this.onShotMoved(e)
                    break;
                case 'melee_attacked':
                    this.onMeleeAttacked(e)
                    break;
                case 'creature_moved':
                    this.onCreatureMove(e)
                    break;
            }
        })
    }

    private onDeath(e: Death) {
        const proto = this.proto!!;

        const msgSubject = e.creatureId == proto.id ? 'You' : `<a>#${e.creatureId}</a>`;//todo fix
        const msgVictim = e.victimId == proto.id ? 'You' : `<a>#${e.victimId}</a>`;
        this.onChatMessage(`${msgSubject} kills ${msgVictim} ☠️`);


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

    private onMetrics(e: OpMetrics) {
        const proto = this.proto!!;
        if (proto.id === e.creatureId) {
            proto.update(e)
            return;
        }

        const cr = proto.zoneCreatures.get(e.creatureId);
        if (cr) {
            cr.update(e)
        }
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
        this.actions.push(new OnDamage(ID++, proto, Date.now(), victim, e.amount, e.crit, isProto));

        const msgSubject = e.creatureId == proto.id ? 'You' : `<a>#${e.creatureId}</a>`;//todo fix
        const msgVictim = e.victimId == proto.id ? 'You' : `<a>#${e.victimId}</a>`;
        this.onChatMessage(`${msgSubject} hits ${msgVictim} for ${e.crit ? '💥' : ''}${e.amount}`);

        // ???
        const spell = this.proto.zoneSpells.get(e.spellId);
        if (spell) {
            spell.finished = true;
            this.proto.zoneSpells.delete(e.spellId);
            this.audio.play('damage_fireball.ogg')
        }
    }

    private onChatMessage(msg: string) {
        let p: HTMLParagraphElement = document.createElement("p");
        const time = new Date();
        // p.innerHTML = `${dt.format(time)} <a>#${e.id}</a> hits <a>#${e.victimId}</a> for ${e.amount}`
        p.innerHTML = msg
        this.chat.append(p);
        this.chat.scrollTo(0, this.chat.scrollHeight);
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
        this.movements.onFrame(time)
        this.spells.onFrame(time);
    }

    getActions(): Act[] {
        return this.actions.splice(0);
    }

    private addPlayer(ac: ApiCreature): Creature {
        const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
        const m = ac.metrics;
        const mm = new Metrics(m.lvl, m.exp, m.maxLife, m.life, m.name);
        const c = new Player(ac.id, mm, o);
        // this.creatures.set(c.id, c);
        return c;
    }

    private addCreature(ac: ApiCreature): Creature {
        const o = new Orientation(null, ac.sight, 0, 0.0, ac.x, ac.y);
        const m = ac.metrics;
        const mm = new Metrics(m.lvl, m.exp, m.maxLife, m.life, m.name);
        const c = new CreatureObject(ac.id, mm, o);
        // this.creatures.set(c.id, c);
        return c;
    }

    onMovingChanged(status: StatusMoving, dir: Dir | undefined, sight: Dir) {

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
        if (e.creatureId === proto.id) return;

        const source = proto.zoneCreatures.get(e.creatureId);
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
                    id: e.creatureId,
                    isPlayer: true,
                    x: e.x,
                    y: e.y,
                    sight: e.sight,
                    direction: e.mv,
                    metrics: new Metrics(-1, -1, 100, 100, "#" + e.creatureId),
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
