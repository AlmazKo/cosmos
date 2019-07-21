import { Dir } from '../constants';

export interface MovingListener {
  onStartMoving(moving: Dir): void;

  onChangeSight(sight: Dir): void;

  onChangeMoving(moving: Dir): void;

  onStopMoving(): void;
}
