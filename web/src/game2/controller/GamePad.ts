import { Dir } from '../constants';
import { Game } from '../engine/Game';
import { Trait, Traits } from '../Trait';
import { HotKey, Key } from './controls';
import { MovingAggregator } from './MovingAggregator';

const movingButtons: { [index: number]: Dir } = {
  14: Dir.WEST,
  12: Dir.NORTH,
  15: Dir.EAST,
  13: Dir.SOUTH
};

// //dualShock buttons
const TRIANGLE = new Key(3, '∆', 'ico_btn_triangle');
const CIRCLE = new Key(1, '○', 'ico_btn_circle');
const CROSS = new Key(0, '×', 'ico_btn_cross');
const SQUARE = new Key(2, '□', 'ico_btn_square');

const hotKeys = new Map<uint, HotKey>();
hotKeys.set(TRIANGLE.code, new HotKey(TRIANGLE, Traits.melee));
hotKeys.set(CIRCLE.code, new HotKey(CIRCLE, Traits.shot));
hotKeys.set(CROSS.code, new HotKey(CROSS, Traits.fireshock));
hotKeys.set(SQUARE.code, new HotKey(SQUARE, Traits.fireball));

export const gamepadSchema = new Map<Key, Trait>();
gamepadSchema.set(TRIANGLE, Traits.fireshock);
gamepadSchema.set(CIRCLE, Traits.shot);
gamepadSchema.set(CROSS, Traits.melee);
gamepadSchema.set(SQUARE, Traits.fireball);

export class GamePad {
  private readonly _presses: uint[] = [];
  private dir: Dir | undefined;

  constructor(
    private readonly moving: MovingAggregator,
    private readonly game: Game
  ) {
    setInterval(() => {
      const g = navigator.getGamepads()[0];
      if (!g) return;

      const leftX = g.axes[0];
      const leftY = g.axes[1];
      const barrier = 0.5;
      if (leftX > barrier && this.dir != Dir.EAST && leftX > Math.abs(leftY)) {
        this.moving.direction(Dir.EAST)
        this.dir = Dir.EAST;
      } else if (leftX < -barrier && this.dir != Dir.WEST && -leftX > Math.abs(leftY)) {
        this.moving.direction(Dir.WEST)
        this.dir = Dir.WEST;
      } else if (leftY > barrier && this.dir != Dir.SOUTH && leftY > Math.abs(leftX)) {
        this.moving.direction(Dir.SOUTH)
        this.dir = Dir.SOUTH;
      } else if (leftY < -barrier && this.dir != Dir.NORTH && -leftY > Math.abs(leftX)) {
        this.moving.direction(Dir.NORTH)
        this.dir = Dir.NORTH;
      }

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
      this.moving.press(movingButtons[b]);
    } else {
      const key = hotKeys.get(b);
      console.log(key)
      if (key) {
        this.game.onAction(key.trait);
      }
    }
  }

  private onKeyup(b: uint) {
    if (b >= 12 && b <= 15) {
      this.moving.release(movingButtons[b]);
    }
  }
}
