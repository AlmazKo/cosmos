import {Dir} from '../constants';
import {Actor} from '../engine/Actor';
import {Action} from './Action';

export class ShotSpell implements Action {
  constructor(
    public readonly time: tsm,
    public readonly id: uint,
    public readonly creature: Actor,
    public readonly speed: speed,
    // public readonly distance: uint,
    public readonly initX: pos,
    public readonly initY: pos,
    public readonly direction: Dir,
  ) {

  }
  public finished = false;
}
