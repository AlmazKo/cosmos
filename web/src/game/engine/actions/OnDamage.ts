import {Act} from '../Act';
import {Actor} from '../Actor';

export class OnDamage implements Act {

  constructor(readonly id: uint,
              readonly actor: Actor,
              readonly startTime: tsm,
              readonly victim: Actor,
              readonly amount: uint,
              readonly crit: boolean,
              readonly isProto: boolean
              ) {

  }

}
