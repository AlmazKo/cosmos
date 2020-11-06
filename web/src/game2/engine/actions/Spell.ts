import { FireballSpell } from '../../../game/actions/FireballSpell';
import { Act } from '../Act';
import { Creature } from '../Creature';

export class Spell implements Act {

  constructor(readonly id: uint,
              readonly creature: Creature,
              readonly startTime: tsm,
              readonly spell: FireballSpell) {

  }

}
