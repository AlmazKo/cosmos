import { Dir, dirToArrow } from '../constants';
import { Focus } from './Moving';

export class Orientation {
  /** @deprecated */
  next: Focus | undefined;

  constructor(
    public move: Dir | null,
    public sight: Dir,
    public vel: velocity,
    public shift: float,
    public x: pos,
    public y: pos
  ) {
  }

  toString() {
    return `${dirToArrow(this.move)}${dirToArrow(this.sight)} Δ${this.shift.toFixed(2)} ${this.x};${this.y}`
  }

  // isBackwards(): boolean {
  //   return this.sight % 2 === this.move % 2;
  // }

  stop() {
    this.next = undefined;
    this.vel = 0;
    this.move = null;
    this.shift = 0;
    console.log("Stopped", this);
  }

  setPosition(nextX: pos, nextY: pos) {
    this.x = nextX;
    this.y = nextY;
  }
}
