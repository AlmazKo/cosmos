import { Metrics } from '../../game/Metrics';
import { Creature } from './Creature';
import { Orientation } from './Orientation';

export class CreatureObject implements Creature {

  constructor(
    public readonly id: uint,
    public readonly metrics: Metrics,
    public readonly orientation: Orientation) {
  }


}
