import {OpMetrics} from '../api/ApiMessage';
import {Metrics} from './Metrics';
import {Actor} from './Actor';
import {Orientation} from './Orientation';

export class ActorObject implements Actor {

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
