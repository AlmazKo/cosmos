import { Dir, dirToArrow, NO } from '../constants';
import { Focus } from './Moving';

export class Orientation {
  next: Focus | undefined;

  constructor(
    public move: Dir,
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

  isBackwards(): boolean {
    return this.sight % 2 === this.move % 2;
  }


  useNext() {
    if (this.next === undefined) return;

    this.sight = this.next.sight;
    this.move  = this.next.moving;
    this.vel   = this.next.vel;
    this.shift = 0;
    this.next  = undefined;
  }

  setNext(moving: Dir, sight: Dir, vel: velocity) {
    this.next = {moving: moving, sight: sight, vel: vel};
  }

  setMoving(moving: Dir, sight: Dir, vel: velocity) {
    if (this.next === undefined) {
      this.move  = moving;
      this.sight = sight;
      this.vel   = vel;
    } else {
      this.setNext(moving, sight, vel);
    }
  }


  stop() {
    this.next  = undefined;
    this.vel   = 0;
    this.move  = NO;
    this.shift = 0;
  }


  setPosition(nextX: pos, nextY: pos) {
    this.x = nextX;
    this.y = nextY;
  }
}
