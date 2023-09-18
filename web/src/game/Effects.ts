import { Camera } from '../game2/render/Camera';
import { Effect } from './Effect';
import { TileDrawable } from './TileDrawable';
import { TilePainter } from './TilePainter';

export class Effects implements TileDrawable {
  private effects = [] as Array<Effect>;

  push(effect: Effect) {
    this.effects.push(effect)
  }

  find(spellId: uint): Effect | undefined {
    return this.effects.find(e => e.id == spellId);
  }


  draw(time: DOMHighResTimeStamp, bp: TilePainter, camera: Camera) {

    //todo
    this.effects.forEach((it: Effect) => {
      it.draw(time, bp, camera)
    });

    //fixme optimize?
    this.effects = this.effects.filter(b => !b.isFinished)
  }
}
