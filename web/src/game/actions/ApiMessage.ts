export type ActionName = 'appear' | 'appear_obj'
export type Type = 'SPELL' | ''


export type ObjAppear = { id: uint, x: pos, y: pos, tileId: index }

export interface ApiMessage {
  readonly id: uint
  readonly action: ActionName
  readonly type: Type
  readonly data: any
}
