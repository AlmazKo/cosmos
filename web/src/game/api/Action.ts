import {Actor} from '../engine/Actor';

export interface Action {
  readonly id: uint,
  readonly creature: Actor,
  readonly time: tsm,

}
