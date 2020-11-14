import { Dir } from '../../game2/constants';

export type ActionName = 'appear' | 'appear_obj' | 'creature_moved' | 'creature_hid' | 'fireball_moved' | 'damage' | 'death'
export type Type = 'SPELL' | ''

export type uid = uint;


export type Appear = { userId: uid, x: pos, y: pos, mv: Dir | null, sight: Dir, life:uint }
export type ObjAppear = { id: uint, x: pos, y: pos, tileId: index }
export type CreatureMoved = { id: uint, mv: Dir | null, sight: Dir, x: pos, y: pos, speed: speed,offset:uint, creatureId: uid }
export type FireballMoved = { spellId: uint, dir: Dir, x: pos, y: pos, speed: speed, sourceId: uid, finished: boolean }
export type CreatureHid = { id: uint, creatureId: uid }
export type Damage = { id: uint, victimId: uid, amount:uint, spellId:uint, crit: boolean}
export type Death = { id: uint, victimId: uid}

export interface ApiMessage {
  readonly id: uint
  readonly action: ActionName
  readonly type: Type
  readonly data: any
}
