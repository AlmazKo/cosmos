import { FireballSpell } from '../../game/actions/FireballSpell';
import { Metrics } from '../../game/Metrics';
import { Dir } from '../constants';
import { Spell } from './actions/Spell';
import { Creature } from './Creature';
import { Orientation } from './Orientation';

export class Player implements Creature {

  readonly zoneObjects = new Map<uint, { x: pos, y: pos, tileId: index }>()
  readonly zoneCreatures = new Map<uint, Creature>()
  readonly zoneSpells = new Map<uint, FireballSpell>()

  constructor(
    public readonly id: uint,
    public readonly metrics: Metrics,
    public readonly orientation: Orientation) {
  }

  isDead(): boolean {
    return this.metrics.life <= 0;
  }
}
