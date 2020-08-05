import { LoopAnimator } from '../../anim/Animator';
import { Animators } from '../../anim/Animators';
import { Dir, NO } from '../constants';
import { Game } from './Game';
import { Orientation } from './Orientation';

export class ProtoMoving {

  private animators = new Animators();

  constructor(private readonly o: Orientation,
              private readonly game: Game) {

  }

  onFrame(time: DOMHighResTimeStamp) {
    this.animators.run(time);

    const o = this.o;
    if (o.move && !this.animators.has("step")) {
      console.log("Start move");
      this.animators.set("step", new LoopAnimator(o.vel,
        (f, i, isNewCycle) => this.onUpdate(f, i, isNewCycle)
      ));
    }
  }

  onUpdate(f: float, i: index, isNewCycle: boolean): uint {

    const o = this.o;
    // let move = o.move;

    if (!isNewCycle) {
      if (o.move === Dir.NORTH || o.move === Dir.EAST) {
        o.shift = f;
      } else {
        o.shift = -f;
      }
      return o.vel;
    }


    console.log("New cycle");

    let nextX = o.x, nextY = o.y;

    switch (o.move) {
      case Dir.WEST:
        nextX = o.x - 1;
        break;
      case Dir.EAST:
        nextX = o.x + 1;
        break;
      case Dir.NORTH:
        nextY = o.y - 1;
        break;
      case Dir.SOUTH:
        nextY = o.y + 1;
        break;
    }

    o.setPosition(nextX, nextY);

    if (!this.game.world.canStep(nextX, nextY, o.move)) {
      o.stop();
      return o.vel;
    }

    if (o.next !== undefined && o.next.moving == 0) {
      o.stop();
      return o.vel;
    }

    if (o.next !== undefined) {
      o.useNext();
    }

    if (o.move === Dir.NORTH || o.move === Dir.EAST) {
      o.shift = f;
    } else {
      o.shift = -f;
    }

    return o.vel;
  }


  // private static stop(o: Orientation) {
  //   console.log("Stop move animation");
  //   o.next  = undefined;
  //   o.vel   = 0;
  //   o.move  = NO;
  //   o.shift = 0;
  //   return 0
  // }
}
