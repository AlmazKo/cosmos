import {Dir} from '../constants';

export type Type = 'SPELL' | ''

export type uid = uint;
export type ActorId = uint;


export type Appear = { userId: uid, x: pos, y: pos, mv: Dir | null, sight: Dir, lvl: uint, life: uint, map: string }
export type ProtoAppear = { userId: uid, x: pos, y: pos, sight: Dir, world: string }
export type Obj = { id: uint, x: pos, y: pos, tile: index }
export type ActorMoved = { mv: Dir | null, sight: Dir, x: pos, y: pos, speed: speed, offset: uint, actor: uid }
export type Fireball = { spell: uint, dir: Dir, x: pos, y: pos, speed: speed, finished: boolean }
export type Shot = { spell: uint, dir: Dir, x: pos, y: pos, speed: speed, /*userId: uid,*/ finished: boolean }
export type MeleeAttack = { spell: uint, source: uid }
export type Disappear = { actor: uid }
export type Damage = { source: ActorId, victim: ActorId, amount: uint, spell: uint, crit: boolean }
export type Death = { source: uid, victim: uid }
export type OpMetrics = { actor: uid, lvl: uint, life: uint, maxLife: uint, exp: uint }

export interface ApiMessage {
    readonly id: uint
    readonly action: string
    readonly type: Type
    readonly data: any
}


type MapperFunction = (m: any) => any;

export interface ApiMapper {
    [key: string]: MapperFunction;
}


export const API_MAPPER: ApiMapper = {
    'proto_appear': (m: any): ProtoAppear => (
        {userId: m.userId, world: m.world, x: m.x, y: m.y, sight: m.sight}
    ),
    'appear': (m: any): Appear => (
        {userId: m.userId, x: m.x, y: m.y, mv: m.dir ? m.dir : null, sight: m.sight, lvl: m.lvl, life: m.life, map: m.map}
    ),
    'obj': (m: any): Obj => (
        {id: m.id, x: m.x, y: m.y, tile: m.tileId}
    ),
    'metrics': (m: any): OpMetrics => (
        {actor: m.actorId, lvl: m.lvl, life: m.life, maxLife: m.maxLife, exp: m.exp}
    ),
    'disappear': (m: any): Disappear => (
        {actor: m.actorId}
    ),
    'damage': (m: any): Damage => (
        {source: m.sourceId, victim: m.victimId, amount: m.amount, spell: m.spellId, crit: m.crit}
    ),
    'death': (m: any): Death => (
        {source: m.sourceId, victim: m.victimId}
    ),
    'fireball': (m: any): Fireball => (
        {spell: m.spellId, dir: m.dir, x: m.x, y: m.y, speed: m.speed, finished: m.finished}
    ),
    'shot': (m: any): Shot => (
        {spell: m.spellId, dir: m.dir, x: m.x, y: m.y, speed: m.speed, finished: m.finished}
    ),
    'melee_attack': (m: any): MeleeAttack => (
        {spell: m.spellId, source: m.sourceId}
    ),
    'move': (m: any): ActorMoved => (
        {actor: m.actorId, x: m.x, y: m.y, offset: m.offset, speed: m.speed, mv: m.mv, sight: m.sight}
    ),
}