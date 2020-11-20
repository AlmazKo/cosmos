import { Dir } from '../constants';
import { MovingAggregator } from '../controller/MovingAggregator';
import { Moving2 } from './Moving2';
import { MovingListener } from './MovingListener';

export class Moving implements MovingAggregator {
  private listener: MovingListener = null!!;
  private mv: Moving2 = new Moving2();

  listen(listener: MovingListener) {
    this.listener = listener;
  }

  press(dir: Dir) {
    const m = this.mv.press(dir);
    //debug console.log('press ', m)
    if (m) {
      this.listener.onMovingChanged(m.status, m.move, m.sight)
    }
  }

  direction(dir: Dir) {
    console.log(dir)
    const m = this.mv.changeSight(dir);
    this.listener.onMovingChanged(m.status, m.move, m.sight);
  }

  release(dir: Dir) {
    const m = this.mv.release(dir);
    //debug console.log('release ', m)
    if (m) {
      this.listener.onMovingChanged(m.status, m.move, m.sight)
    }
  }

  toString() {
    return this.mv.toString()
  }
}



