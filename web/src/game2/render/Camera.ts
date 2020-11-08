import { Dir, NO } from '../constants';
import { Orientation } from '../engine/Orientation';
import { CELL } from './constants';

export class Camera {
  //top-left corner of central cell
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

  toPosX(x: px): pos {
    const shift = Math.ceil((this.absoluteX - x) / CELL);
    return this.target.x - shift;
  }

  toPosY(y: px): pos {
    const shift = Math.ceil((this.absoluteY - y) / CELL);

    return this.target.y - shift;
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

  toX2(ort: Orientation): px {
    const mv = this.target.move;
    const base = (ort.x - this.target.x) * CELL + this.absoluteX;

    let shiftX: px = 0;
    if (ort.move == Dir.WEST) {
      shiftX = -ort.shift * CELL
    } else if (ort.move === Dir.EAST) {
      shiftX = ort.shift * CELL
    }

    if (mv === Dir.WEST) {
      return base + this.target.shift * CELL + shiftX;
    } else if (mv === Dir.EAST) {
      return base - this.target.shift * CELL + shiftX;
    } else {
      return base + shiftX;
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

  toY2(ort: Orientation): px {
    const mv = this.target.move;
    const base = (ort.y - this.target.y) * CELL + this.absoluteY;

    let shiftY: px = 0;
    if (ort.move == Dir.SOUTH) {
      shiftY = ort.shift * CELL
    } else if (ort.move === Dir.NORTH) {
      shiftY = -ort.shift * CELL
    }


    if (mv === Dir.SOUTH) {
      return base - this.target.shift * CELL + shiftY;
    } else if (mv === Dir.NORTH) {
      return base + this.target.shift * CELL + shiftY;
    } else {
      return base + shiftY;
    }
  }
}
