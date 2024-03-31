import { ApiMessage } from './ApiMessage';

export interface Package {
  readonly tick: uint,
  readonly tickTimeMs: tsm,
  readonly ops: ApiMessage[],

}
