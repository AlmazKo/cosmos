import {Trait} from '../../Trait';
import {Act} from '../Act';
import {Actor} from '../Actor';

export class ActivateTrait implements Act {

  constructor(readonly id: uint,
              readonly actor: Actor,
              readonly startTime: tsm,
              readonly trait: Trait) {

  }

}
