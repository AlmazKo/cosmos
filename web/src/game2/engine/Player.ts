import { Metrics } from '../../game/Metrics';
import { Creature } from './Creature';
import { Orientation } from './Orientation';

export class Player implements Creature {

  readonly zoneObjects = new Map<number, { x: pos, y: pos, tileId: index }>()

  constructor(
    public readonly id: uint,
    public readonly metrics: Metrics,
    public readonly orientation: Orientation) {
  }


}
