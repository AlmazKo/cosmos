import { Dir } from '../constants';
import { StatusMoving } from './Moving2';

export interface MovingListener {
  onMovingChanged(status: StatusMoving, dir: Dir, sight: Dir): void;
}
