import { Dir } from '../constants';

export interface MovingAggregator {
  press(dir: Dir): void

  release(dir: Dir): void

  direction(dir: Dir): void
}
