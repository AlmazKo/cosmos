import { OpMetrics } from '../../game/actions/ApiMessage';
import { Metrics } from '../../game/Metrics';
import { Creature } from './Creature';
import { Orientation } from './Orientation';

export class CreatureObject implements Creature {

  constructor(
    public readonly id: uint,
    public readonly metrics: Metrics,
    public readonly orientation: Orientation) {
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

  update(e: OpMetrics) {
    this.metrics.lvl = e.lvl;
    this.metrics.life = e.life;
    this.metrics.maxLife = e.maxLife;
  }

}
