import { Animator } from '../../../anim/Animator';
import { Effect } from '../../../game/Effect';
import { style } from '../../../game/styles';
import { TilePainter } from '../../../game/TilePainter';
import { Camera } from '../Camera';
import { HCELL } from '../constants';

export class DamageEffect implements Effect {
  readonly id = 0;
  isFinished = false;
  private readonly posX: pos;
  private readonly posY: pos;
  private readonly amount: uint;

  private shift: px = 0;
  private anim: Animator;
  private f: float = 0;
  private crit: boolean;

  constructor(dmg: uint, crit: boolean, x: pos, y: pos) {
    this.amount = dmg;
    this.crit = crit;
    this.posX = x;
    this.posY = y;
    this.anim = new Animator(300, f => {
      this.f = f;
      if (f >= 1) this.isFinished = true;
    });
  }

  draw(time: DOMHighResTimeStamp, p: TilePainter) {

  }

  draw2(time: DOMHighResTimeStamp, bp: TilePainter, camera: Camera) {
    const x = camera.toX(this.posX) + HCELL;
    const y = camera.toY(this.posY);

    this.anim.run(time);

    if (this.crit) {
      const shiftY = this.f * 40; //set pixels clearly
      bp.p.text("" + this.amount, x + 1, y - shiftY + 1, style.dmgCritText2);
      bp.p.text("" + this.amount, x, y - shiftY, style.dmgCritText);
    } else {
      const shiftY = this.f * 20; //set pixels clearly
      bp.p.text("" + this.amount, x + 1, y - shiftY + 1, style.dmgText2);
      bp.p.text("" + this.amount, x, y - shiftY, style.dmgText);
    }


  }


  stop(): void {
    this.anim.finish();
    this.isFinished = true;
  }
}
