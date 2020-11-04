import { Dir, NO } from '../constants';
import { Orientation } from '../engine/Orientation';
import { CELL } from './constants';

export class Camera {
  absoluteX: px = 0;
  absoluteY: px = 0;
  target: Orientation;

  constructor(public offset: floatShare = 0.0) {
    this.target = new Orientation(NO, NO, 0, offset, 0, 0)
  }

  setTarget(orientation: Orientation) {
    this.target = orientation;
    // this.target.setPosition(1, 1);
  }

  toX(pos: pos): px {
    const mv = this.target.move;
    const base = (pos - this.target.x) * CELL + this.absoluteX;

    if (mv === Dir.WEST) {
      return base + this.target.shift * CELL;
    } else if (mv === Dir.EAST) {
      return base - this.target.shift * CELL;
    } else {
      return base;
    }
  }

  toY(pos: pos): px {
    const mv = this.target.move;
    const base = (pos - this.target.y) * CELL + this.absoluteY;

    if (mv === Dir.SOUTH) {
      return base - this.target.shift * CELL;
    } else if (mv === Dir.NORTH) {
      return base + this.target.shift * CELL;
    } else {
      return base;
    }
  }
}
