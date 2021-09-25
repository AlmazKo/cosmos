import { OpMetrics } from '../../game/actions/ApiMessage';
import { FireballSpell } from '../../game/actions/FireballSpell';
import { Metrics } from '../../game/Metrics';
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

  update(e: OpMetrics) {
    this.metrics.lvl = e.lvl;
    this.metrics.life = e.life;
    this.metrics.maxLife = e.maxLife;
    this.metrics.exp = e.exp;
  }

  isDead(): boolean {
    return this.metrics.life <= 0;
  }

  x(): pos {
    return this.orientation.x;
  }

  y(): pos {
    return this.orientation.y;
  }

}
