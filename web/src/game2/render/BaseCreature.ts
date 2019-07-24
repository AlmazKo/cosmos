import { Animator, Delay, LoopAnimator } from '../../anim/Animator';
import { Animators } from '../../anim/Animators';
import { TileDrawable } from '../../game/TileDrawable';
import { TilePainter } from '../../game/TilePainter';
import { Dir, NOPE } from '../constants';
import { Creature } from '../engine/Creature';
import { Orientation } from '../engine/Orientation';
import { Camera } from './Camera';
import { CELL } from './constants';


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
  private f: floatShare      = 0;

  constructor(c: Creature) {
    this.orientation = c.orientation;


  }

  draw(time: DOMHighResTimeStamp, bp: TilePainter) {

  }

  draw2(time: DOMHighResTimeStamp, bp: TilePainter, camera: Camera) {


    const o = this.orientation;

    if (o.moving !== NOPE && !this.animators.has("step")) {
      this.startMoving()
    }

    // if (this.orientation.moving === 0 && this.animators.has("step")) {
    //   this.stopMoving();
    // }

    this.animators.run(time);


    const x = camera.absoluteX;
    const y = camera.absoluteY;
    let sy  = map[o.sight];
    let sx  = Math.floor(this.f * 9) * 64;
    // drawLifeLine(bp.toInDirect(x, y), this);

    let sw = 64, sh = 64;
    // if (this.showInstantSpell) {
    //   sx = 7 * 16;
    // } else if (this.meleeFactor) {
    //   sy += 32 * 4;
    //   sw = 16;
    //   sx = Math.floor(this.meleeFactor * 4) * 32 + 8;
    // }

    bp.drawTo("ch", sx, sy, sw, sh, x, y, CELL, CELL);
  }

  startMoving() {
    // console.log("startMoving" + this.orientation);
    this.animators.interrupt("step");
    const o = this.orientation;

    const defDuration = 300;
    let dur           = defDuration;

    const movement = new LoopAnimator(dur, (f, i, isNewCycle) => {

      this.f = f;

      let dr = o.moving;


      // console.log("Anim " + o, dur);

      if (!isNewCycle) {
        if (dr === Dir.NORTH || dr === Dir.EAST) {
          o.shift = f;
        } else {
          o.shift = -f;
        }
        return dur;
      }
      console.log("New cycle")

      switch (dr) {
        case Dir.WEST:
          o.x--;
          break;
        case Dir.EAST:
          o.x++;
          break;
        case Dir.NORTH:
          o.y--;
          break;
        case Dir.SOUTH:
          o.y++;
          break;
      }


      if (o.next !== undefined && o.next.moving == 0) {
        console.log(o);
        o.next   = undefined;
        o.moving = NOPE;
        o.shift  = 0;
        return 0
      }


      if (o.next !== undefined) {
        dr       = o.next.moving;
        o.sight  = o.next.sight;
        o.moving = o.next.moving;
        o.shift  = 0;
        if (o.moving === o.sight) {
          dur = defDuration;
        } else if (o.isBackwards()) {
          dur = defDuration * 4;
        } else {
          dur = defDuration * 1.5;
        }
        console.log("Change orientation " + o);
        console.log("set duration", dur);


        o.next = undefined;
      }

      if (dr === Dir.NORTH || dr === Dir.EAST) {
        o.shift = f;
      } else {
        o.shift = -f;
      }

      return dur;
    });

    this.animators.set("step", movement);
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

  move() {

  }


  melee() {
    this.animators.set("melee",
      new Animator(250, f => this.meleeFactor = f),
      () => this.meleeFactor = 0);
  }

  instantSpell() {
    this.showInstantSpell = true;
    this.animators.set("instant_spell",
      new Delay(100),
      () => this.showInstantSpell = false);
  }
}
