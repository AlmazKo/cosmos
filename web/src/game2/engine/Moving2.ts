import { Dir, dirToArrow } from '../constants';


export interface Focus {
  moving: Dir,
  sight: Dir,
  vel: velocity
}


export enum StatusMoving {
  STOP         = 0,
  START        = 1,
  CHANGE_MOVE  = 2,
  CHANGE_SIGHT = 3
}

export interface Mv {
  status: StatusMoving,
  move: Dir | null; //not moving
  sight: Dir;
}


export class Moving2 {
  private _moving: Dir | null = null;
  private _sight: Dir | null = null;

  press(dir: Dir): Mv | null {
    if (this._moving === null) {
      this._moving = dir;
      console.log("onStartMoving " + this);
      return {status: StatusMoving.START, move: this._moving, sight: this._moving};
    } else if (this._sight === null) {
      this._sight = dir;
      console.log("onChangeSight " + this);
      return {status: StatusMoving.CHANGE_SIGHT, move: this._moving, sight: this._sight};
    } else {
      //ignore the 3d action
      return null
    }
  }

  release(dir: Dir): Mv | null {
    if (this._moving === dir && this._sight === null) {
      this._moving = null;
      console.log("onStopMoving " + this);
      return {status: StatusMoving.STOP, move: this._moving, sight: dir};
    } else if (this._sight === dir) {
      this._sight = null;
      console.log("onChangeSight " + this);
      return {status: StatusMoving.CHANGE_SIGHT, move: this._moving, sight: this._moving};
    } else if (this._moving === dir) {
      this._moving = this._sight;
      this._sight = null;
      console.log("onChangeMoving " + this);
      return {status: StatusMoving.CHANGE_MOVE, move: this._moving, sight: this._moving};
    }

    return null
  }

  toString() {
    return `${dirToArrow(this._moving)}(${dirToArrow(this._sight)})`
  }
}



