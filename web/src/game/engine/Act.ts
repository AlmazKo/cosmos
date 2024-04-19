import {Actor} from './Actor';

export interface Act {
  readonly id: uint,
  readonly actor: Actor,
  readonly startTime: tsm,
}
