import { Animator } from '../../../anim/Animator';
import { FontStyle } from '../../../draw/FontStyleAcceptor';
import { Effect } from '../../../game/Effect';
import { style } from '../../../game/styles';
import { TilePainter } from '../../../game/TilePainter';
import { OnDamage } from '../../engine/actions/OnDamage';
import { Camera } from '../Camera';
import { CELL, HCELL } from '../constants';

export class DamageEffect implements Effect {
  readonly id = 0;
  isFinished = false;
  private readonly posX: pos;
  private readonly posY: pos;

  private anim: Animator;
  private f: float = 0;

  constructor(private event: OnDamage) {
    this.posX = event.victim.orientation.x;
    this.posY = event.victim.orientation.y;
    this.anim = new Animator(event.crit ? 500 : 300, f => {
      this.f = f;
      if (f >= 1) this.isFinished = true;
    });
  }

  draw(time: DOMHighResTimeStamp, p: TilePainter) {

  }

  draw2(time: DOMHighResTimeStamp, bp: TilePainter, camera: Camera) {
    const x = camera.toX(this.posX) + HCELL;
    const y = camera.toY(this.posY);
    const amount = this.event.amount;
    this.anim.run(time);

    if (this.event.crit) {
      const shiftY = this.f * CELL; //set pixels clearly

      const txtStyle: Partial<FontStyle> = this.event.isProto ? {...style.dmgCritText, style: '#ff5252'} : style.dmgCritText;
      bp.p.text("" + amount, x + 1, y - shiftY + 1, style.dmgCritText2);
      bp.p.text("" + amount, x, y - shiftY, txtStyle);
    } else {
      const shiftY = this.f * HCELL; //set pixels clearly
      const txtStyle: Partial<FontStyle> = this.event.isProto ? {...style.dmgText, style: '#ff5252'} : style.dmgText;
      bp.p.text("" + amount, x + 1, y - shiftY + 1, style.dmgText2);
      bp.p.text("" + amount, x, y - shiftY, txtStyle);
    }
  }


  stop(): void {
    this.anim.finish();
    this.isFinished = true;
  }
}
