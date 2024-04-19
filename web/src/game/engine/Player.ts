import {OpMetrics} from '../api/ApiMessage';
import {FireballSpell} from '../api/FireballSpell';
import {Metrics} from './Metrics';
import {Actor} from './Actor';
import {Orientation} from './Orientation';

export class Player implements Actor {

  readonly zoneObjects = new Map<uint, { x: pos, y: pos, tile: index }>()
  readonly zoneActors = new Map<uint, Actor>()
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
