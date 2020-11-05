import { Dir } from '../../constants';
import { Act } from '../Act';
import { Creature } from '../Creature';

export class StartMoving implements Act {

  constructor(readonly id: uint,
              readonly creature: Creature,
              readonly startTime: tsm,
              readonly speed: ms,
              readonly dir: Dir) {

  }

}
