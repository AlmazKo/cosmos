import {OpMetrics} from '../api/ApiMessage';
import {Metrics} from './Metrics';
import {Orientation} from './Orientation';

export interface Actor {
  readonly id: uint;
  readonly orientation: Orientation;
  readonly metrics: Metrics;

  isDead(): boolean;

  update(e: OpMetrics) :void;

  x(): pos

  y(): pos
}
