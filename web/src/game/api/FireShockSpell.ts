import {Actor} from '../engine/Actor';
import {Action} from './Action';

export class FireShockSpell implements Action {
  readonly posX: pos;
  readonly posY: pos;
  readonly creature: Actor;

  constructor(
    public readonly time: tsm,
    public readonly id: uint,
    creature: Actor,
    public readonly duration: uint,
    public readonly distance: uint) {

    this.creature = creature;
    this.posX     = creature.orientation.x;
    this.posY     = creature.orientation.y;
  }

}
