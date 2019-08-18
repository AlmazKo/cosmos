import { Dir } from '../constants';
import { MovingAggregator } from './MovingAggregator';

const Buttons: { [index: number]: Dir } = {
  14: Dir.WEST,
  12: Dir.NORTH,
  15: Dir.EAST,
  13: Dir.SOUTH
};

const moving = [12, 13, 14, 15];

export class GamePad {


  private _presses: uint[] = [];

  constructor(
    private readonly moving: MovingAggregator,
  ) {

    console.log("Start Gamepad");

    setInterval(() => {
      const g = navigator.getGamepads()[0];
      if (!g) return;

      const btns = g.buttons;

      for (let i = 12; i <= 15; i++) {
        const pressed = btns[i].pressed;

        if (pressed && !this._presses.contains(i)) {
          this._presses.push(i);
          this.onPress(Buttons[i])
        }

        if (!pressed && this._presses.contains(i)) {
          this._presses.remove(i);
          this.onKeyup(Buttons[i])
        }
      }

      // console.log(g.buttons);
    }, 10)
  }


  private onPress(dir: Dir) {
    this.moving.press(dir);
  }

  private onKeyup(dir: Dir) {
    this.moving.release(dir);
  }
}
