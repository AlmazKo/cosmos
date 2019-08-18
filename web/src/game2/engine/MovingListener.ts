import { Dir } from '../constants';

export interface MovingListener {
  onStartMoving(moving: Dir, sight: Dir): void;

  // onChangeSight(move: Dir, sight: Dir): void;

  onChangeMoving(moving: Dir, sight: Dir): void;

  onStopMoving(moving: Dir, sight: Dir): void;
}
