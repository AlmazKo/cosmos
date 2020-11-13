import { Dir, dirToArrow } from '../constants';

export class Orientation {


  public offset: number = 0;

  constructor(
    public move: Dir | null,
    public sight: Dir,
    public speed: speed,
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
    this.speed = 0;
    this.move = null;
    this.shift = 0;
    this.offset = 0;
    //todo debug console.log("Stopped", this);
  }

  setPosition(nextX: pos, nextY: pos) {
    this.x = nextX;
    this.y = nextY;
  }
}
