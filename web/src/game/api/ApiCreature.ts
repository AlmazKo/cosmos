import {Dir} from '../constants';
import {Metrics} from '../engine/Metrics';

export interface ApiCreature {
  id: uint;
  metrics: Metrics;
  isPlayer: boolean,
  x: pos;
  y: pos;
  sight: Dir;
  direction: Dir|null;
  viewDistance: uint;
}
