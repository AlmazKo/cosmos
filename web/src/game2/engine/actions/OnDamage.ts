import { Damage } from '../../../game/actions/ApiMessage';
import { Act } from '../Act';
import { Creature } from '../Creature';

export class OnDamage implements Act {

  constructor(readonly id: uint,
              readonly creature: Creature,
              readonly startTime: tsm,
              readonly victim: Creature,
              readonly amount: uint,
              readonly crit: boolean,
              readonly isProto: boolean
              ) {

  }

}
