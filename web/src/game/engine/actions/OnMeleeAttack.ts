import {Act} from '../Act';
import {Actor} from '../Actor';

export class OnMeleeAttack implements Act {

  constructor(readonly id: uint,
              readonly actor: Actor,
              readonly startTime: tsm) {

  }

}
