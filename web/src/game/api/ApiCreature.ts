import { Dir } from '../../game2/constants';
import { Metrics } from '../Metrics';

export interface ApiCreature {
  id: uint;
  metrics: Metrics;
  isPlayer: boolean,
  x: pos;
  y: pos;
  sight: Dir;
  direction: Dir;
  viewDistance: uint;
}
