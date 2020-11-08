import { Dir } from '../../game2/constants';

export type ActionName = 'appear' | 'appear_obj' | 'creature_moved' | 'creature_hid' | 'fireball_moved'
export type Type = 'SPELL' | ''

export type uid = uint;


export type Appear = { userId: uid, x: pos, y: pos, mv: Dir | null, sight: Dir }
export type ObjAppear = { id: uint, x: pos, y: pos, tileId: index }
export type CreatureMoved = { id: uint, mv: Dir | null, sight: Dir, x: pos, y: pos, speed: speed, creatureId: uid }
export type FireballMoved = { id: uint, dir: Dir, x: pos, y: pos, speed: speed, sourceId: uid, finished: boolean }
export type CreatureHid = { id: uint, creatureId: uid }

export interface ApiMessage {
  readonly id: uint
  readonly action: ActionName
  readonly type: Type
  readonly data: any
}
