import { Act } from '../Act';
import { Creature } from '../Creature';

export class OnMeleeAttack implements Act {

  constructor(readonly id: uint,
              readonly creature: Creature,
              readonly startTime: tsm) {

  }

}
