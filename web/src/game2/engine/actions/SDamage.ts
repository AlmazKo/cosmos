import { Damage } from '../../../game/actions/ApiMessage';
import { Act } from '../Act';
import { Creature } from '../Creature';

export class SDamage implements Act {

  constructor(readonly id: uint,
              readonly creature: Creature,
              readonly startTime: tsm,
              readonly dmg: Damage,
              readonly x: pos,
              readonly y: pos) {

  }

}
