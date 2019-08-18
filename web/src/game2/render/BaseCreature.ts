import { Animator, Delay } from '../../anim/Animator';
import { Animators } from '../../anim/Animators';
import { TileDrawable } from '../../game/TileDrawable';
import { TilePainter } from '../../game/TilePainter';
import { Dir } from '../constants';
import { Creature } from '../engine/Creature';
import { Orientation } from '../engine/Orientation';
import { Camera } from './Camera';
import { CELL, startMoving2 } from './constants';


const map: px[] = [];

map[Dir.NORTH] = 0;
map[Dir.SOUTH] = 128;
map[Dir.EAST]  = 196;
map[Dir.WEST]  = 64;


export class DrawableCreature implements TileDrawable {

  readonly orientation: Orientation;

  private animators          = new Animators();
  private showInstantSpell   = false;
  private meleeFactor: float = 0;

  constructor(c: Creature) {
    this.orientation = c.orientation;
  }

  draw(time: DOMHighResTimeStamp, bp: TilePainter) {

  }

  draw2(time: DOMHighResTimeStamp, bp: TilePainter, camera: Camera) {

    const o = this.orientation;

    // this.animators.run(time);
    const x = camera.absoluteX;
    const y = camera.absoluteY;
    let sy  = map[o.sight];
    let sx  = Math.floor(Math.abs(o.shift) * 9) * 64;
    // drawLifeLine(bp.toInDirect(x, y), this);

    let sw = 64, sh = 64;
    bp.drawTo("ch", sx, sy, sw, sh, x, y, CELL, CELL);
  }

  // onRotated(rotated: boolean) {
  //   this.rotated = rotated;
  // }
  //
  //
  // onStep(step: Step): void {
  //
  //   //todo add server sync
  // }
  //
  // melee() {
  //   this.animators.set("melee",
  //     new Animator(250, f => this.meleeFactor = f),
  //     () => this.meleeFactor = 0);
  // }
  //
  // instantSpell() {
  //   this.showInstantSpell = true;
  //   this.animators.set("instant_spell",
  //     new Delay(100),
  //     () => this.showInstantSpell = false);
  // }
}
