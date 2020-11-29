import { Dir } from '../../game2/constants';
import { Creature } from '../../game2/engine/Creature';
import { Action } from './Action';

export class ShotSpell implements Action {
  constructor(
    public readonly time: tsm,
    public readonly id: uint,
    public readonly creature: Creature,
    public readonly speed: speed,
    // public readonly distance: uint,
    public readonly initX: pos,
    public readonly initY: pos,
    public readonly direction: Dir,
  ) {

  }
  public finished = false;
}
