import {Dir} from '../constants';

export type Type = 'SPELL' | ''

export type uid = uint;


export type Appear =      { userId: uid, x: pos, y: pos, mv: Dir | null, sight: Dir, lvl: uint, life: uint, map: string }
export type ProtoAppear = { userId: uid, x: pos, y: pos, sight: Dir, world: string }
export type ObjAppear = { id: uint, x: pos, y: pos, tileId: index }
export type ActorMoved = { mv: Dir | null, sight: Dir, x: pos, y: pos, speed: speed, offset: uint, actorId: uid }
export type FireballMoved = { spellId: uint, dir: Dir, x: pos, y: pos, speed: speed, finished: boolean }
export type ShotMoved = { spellId: uint, dir: Dir, x: pos, y: pos, speed: speed, /*userId: uid,*/ finished: boolean }
export type MeleeAttacked = { spellId: uint, sourceId: uid }
export type ActorHid = { creatureId: uid }
export type Damage = {  creatureId: uid, victimId: uid, amount: uint, spellId: uint, crit: boolean }
export type Death = { creatureId: uid, victimId: uid }
export type OpMetrics = { creatureId: uid, lvl: uint, life: uint, maxLife: uint, exp: uint }

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
        {userId: m.userId, x: m.x, y: m.y, mv: m.dir, sight: m.sight, lvl: m.lvl, life: m.life, map: m.map}
    ),
    'obj_appear': (m: any): ObjAppear => (
        {id: m.userId, x: m.x, y: m.y, tileId: m.tileId}
    ),
    'metrics': (m: any): OpMetrics => (
        {creatureId: m.creatureId, lvl: m.lvl, life: m.life, maxLife: m.maxLife, exp: m.exp}
    ),
    'actor_hid': (m: any): ActorHid => (
        {creatureId: m.creatureId}
    ),
    'damage': (m: any): Damage => (
        {creatureId: m.creatureId, victimId: m.victimId, amount: m.amount, spellId: m.spellId, crit: m.crit}
    ),
    'death': (m: any): Death => (
        {creatureId: m.creatureId, victimId: m.victimId}
    ),
    'fireball_moved': (m: any): FireballMoved => (
        {spellId: m.spellId, dir: m.dir, x: m.x, y: m.y, speed: m.speed, finished: m.finished}
    ),
    'shot_moved': (m: any): ShotMoved => (
        {spellId: m.spellId, dir: m.dir, x: m.x, y: m.y, speed: m.speed, finished: m.finished}
    ),
    'melee_attacked': (m: any): MeleeAttacked => (
        {spellId: m.spellId, sourceId: m.sourceId}
    ),
    'actor_moved': (m: any): ActorMoved => (
        {actorId: m.actorId, x: m.x, y: m.y, offset: m.offset, speed: m.speed, mv: m.mv, sight: m.sight}
    ),
}