import { Dir } from '../../constants';
import { Action } from './Action';

export interface ChangeMoving extends Action {
  move: Dir,
  sight: Dir,
}
