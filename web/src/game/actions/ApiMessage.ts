import {Dir} from '../../game2/constants';

export type Type = 'SPELL' | ''

export type uid = uint;


export type Appear = { userId: uid, x: pos, y: pos, mv: Dir | null, sight: Dir, lvl: uint, life: uint, map: string }
export type ProtoAppear = { userId: uid, x: pos, y: pos, sight: Dir, map: string }
export type ObjAppear = { id: uint, x: pos, y: pos, tileId: index }
export type CreatureMoved = { id: uint, mv: Dir | null, sight: Dir, x: pos, y: pos, speed: speed, offset: uint, creatureId: uid }
export type FireballMoved = { spellId: uint, dir: Dir, x: pos, y: pos, speed: speed, sourceId: uid, finished: boolean }
export type ShotMoved = { spellId: uint, dir: Dir, x: pos, y: pos, speed: speed, sourceId: uid, finished: boolean }
export type MeleeAttacked = { spellId: uint, creatureId: uid }
export type CreatureHid = { id: uint, creatureId: uid }
export type Damage = { id: uint, creatureId: uid, victimId: uid, amount: uint, spellId: uint, crit: boolean }
export type Death = { id: uint, creatureId: uid, victimId: uid }
export type OpMetrics = { id: uint, creatureId: uid, lvl: uint, life: uint, maxLife: uint, exp:uint }

export interface ApiMessage {
    readonly id: uint
    readonly action: string
    readonly type: Type
    readonly data: any
}
