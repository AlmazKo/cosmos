import { Dir } from '../constants';
import { Game } from '../engine/Game';
import { Trait, Traits } from '../Trait';
import { HotKey, Key } from './controls';
import { MovingAggregator } from './MovingAggregator';

const movingButtons: { [index: number]: Dir } = {
  37: Dir.WEST,
  38: Dir.NORTH,
  39: Dir.EAST,
  40: Dir.SOUTH
};


const BTN_1 = new Key(49, '1');
const BTN_2 = new Key(50, '2');
const BTN_3 = new Key(51, '3');
const BTN_4 = new Key(52, '4');
const BTN_LEFT = new Key(37, '◁');
const BTN_RIGHT = new Key(39, '▷');
const BTN_UP = new Key(38, '△');
const BTN_DOWN = new Key(40, '▽');

const hotKeys = new Map<Key, HotKey>();
hotKeys.set(BTN_1, new HotKey(BTN_1, Traits.melee));
hotKeys.set(BTN_2, new HotKey(BTN_2, Traits.fireball));
hotKeys.set(BTN_3, new HotKey(BTN_3, Traits.fireshock));
hotKeys.set(BTN_4, new HotKey(BTN_4, Traits.shot));


const Buttons: { [index: number]: Key } = {
  49: BTN_1,
  50: BTN_2,
  51: BTN_3,
  52: BTN_4,
  37: BTN_LEFT,
  38: BTN_UP,
  39: BTN_RIGHT,
  40: BTN_DOWN
};


export const keyboardSchema = new Map<Key, Trait>();
keyboardSchema.set(BTN_3, Traits.fireshock);
keyboardSchema.set(BTN_4, Traits.shot);
keyboardSchema.set(BTN_1, Traits.melee);
keyboardSchema.set(BTN_2, Traits.fireball);


export class Keyboard {

  constructor(
    private readonly moving: MovingAggregator,
    private readonly game: Game) {
    window.addEventListener('keydown', e => this.onKeydown(e));
    window.addEventListener('keyup', e => this.onKeyup(e));
  }

  private lastKeyDowns: Dir[] = [];

  private onKeydown(e: KeyboardEvent) {
    const btn = Buttons[e.keyCode];
    if (!btn) return;

    const dir = movingButtons[btn.code]
    if (dir) {
      if (!this.lastKeyDowns.contains(dir)) {
        this.lastKeyDowns.push(dir);
        this.moving.press(dir);
      }
    } else {
      const hotkey = hotKeys.get(btn)!!;
      this.game.onAction(hotkey.trait);
    }
  }

  private onKeyup(e: KeyboardEvent) {
    const btn = Buttons[e.keyCode];
    if (!btn) return;
    // console.log('onKeyup   ', e.keyCode, btn);

    const dir = movingButtons[btn.code]
    if (dir) {
      this.lastKeyDowns.remove(dir);
      this.moving.release(dir);
    }
  }

  private onChanged() {
    // const f = this.move.next();
    // if (f !== undefined) {
    //   console.log('Focus: ', f);
    //   this.listener.onStartMoving(f);
    // }
  }


}
