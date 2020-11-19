import { HotKey } from '../../game/Slot';
import { Traits } from '../../game/Trait';
import { Dir } from '../constants';
import { Game } from '../engine/Game';
import { Key } from './Keyboard';
import { MovingAggregator } from './MovingAggregator';

const Buttons: { [index: number]: Dir } = {
  14: Dir.WEST,
  12: Dir.NORTH,
  15: Dir.EAST,
  13: Dir.SOUTH
};


export const BTN_TRIANGLE = new Key(1, "∆");
export const BTN_CIRCLE = new Key(2, "○");
export const BTN_CROSS = new Key(3, "×");
export const BTN_SQUARE = new Key(4, "□");


export const hotKeys = new Map<uint, HotKey>();
hotKeys.set(0, new HotKey(BTN_TRIANGLE, Traits.fireshock));
hotKeys.set(1, new HotKey(BTN_CIRCLE, Traits.shot));
hotKeys.set(2, new HotKey(BTN_CROSS, Traits.melee));
hotKeys.set(3, new HotKey(BTN_SQUARE, Traits.fireball));

export class GamePad {
  private _presses: uint[] = [];

  constructor(
    private readonly moving: MovingAggregator,
    private readonly game: Game
  ) {

    console.log("Start Gamepad");

    setInterval(() => {
      const g = navigator.getGamepads()[0];
      if (!g) return;

      const btns = g.buttons;

      for (let i = 0; i <= 15; i++) {
        const pressed = btns[i].pressed;

        if (pressed && !this._presses.contains(i)) {
          this._presses.push(i);
          this.onPress(i)
        }

        if (!pressed && this._presses.contains(i)) {
          this._presses.remove(i);
          this.onKeyup(i)
        }
      }

      // console.log(g.buttons);
    }, 10)
  }


  private onPress(b: uint) {
    if (b >= 12 && b <= 15) {
      this.moving.press(Buttons[b]);
    } else {
      const key = hotKeys.get(b);
      if (key) {
        this.game.onAction(key.trait);
      }
    }
  }

  private onKeyup(b: uint) {
    if (b >= 12 && b <= 15) {
      this.moving.release(Buttons[b]);
    }
  }
}
