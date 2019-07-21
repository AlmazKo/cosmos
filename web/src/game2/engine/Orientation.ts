import { Dir, dirToArrow } from '../constants';
import { Focus } from './Moving';

export class Orientation {
  next: Focus | undefined;

  constructor(
    public moving: Dir,
    public sight: Dir,
    public shift: floatShare,
    public x: pos,
    public y: pos
  ) {
  }

  toString() {
    return `${dirToArrow(this.moving)}${dirToArrow(this.sight)} Δ${this.shift.toFixed(2)} ${this.x};${this.y}`
  }

  isBackwards(): boolean {
    return this.sight % 2 === this.moving % 2;
  }

}
