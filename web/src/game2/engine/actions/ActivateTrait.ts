import { Trait } from '../../Trait';
import { Act } from '../Act';
import { Creature } from '../Creature';

export class ActivateTrait implements Act {

  constructor(readonly id: uint,
              readonly creature: Creature,
              readonly startTime: tsm,
              readonly trait: Trait) {

  }

}
