import { Dir, dirToArrow } from '../constants';
import { MovingAggregator } from '../controller/MovingAggregator';
import { MovingListener } from './MovingListener';


export interface Focus {
  moving: Dir,
  sight: Dir
}

export class Moving implements MovingAggregator {
  private listener: MovingListener = null!!;
  private _moving: Dir             = 0;
  private _sight: Dir              = 0;

  listen(listener: MovingListener) {
    this.listener = listener;
  }

  press(dir: Dir) {
    if (this._moving === 0) {
      this._moving = dir;
      console.log("onStartMoving " + this);
      this.listener.onStartMoving(dir);
    } else if (this._sight === 0) {
      this._sight = dir;
      console.log("onChangeSight " + this);
      this.listener.onChangeSight(dir)
    } else {
      //ignore the 3d action
    }
  }

  release(dir: Dir) {
    if (this._moving === dir && this._sight === 0) {
      this._moving = 0;
      console.log("onStopMoving " + this);
      this.listener.onStopMoving();
    } else if (this._sight === dir) {
      this._sight = 0;
      console.log("onChangeSight " + this);
      this.listener.onChangeSight(this._moving)
    } else if (this._moving === dir) {
      this._moving = this._sight;
      this._sight  = 0;
      console.log("onChangeMoving " + this);
      this.listener.onChangeMoving(this._moving)
    }
  }

  toString() {
    return `${dirToArrow(this._moving)}(${dirToArrow(this._sight)})`
  }
}



