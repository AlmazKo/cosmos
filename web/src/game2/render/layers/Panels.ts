import { Animator } from '../../../anim/Animator';
import { Animators } from '../../../anim/Animators';
import { toRGBA } from '../../../canvas/utils';
import { CanvasContext } from '../../../draw/CanvasContext';
import { Drawable } from '../../../game/Drawable';
import { Key } from '../../controller/controls';
import { ActivateTrait } from '../../engine/actions/ActivateTrait';
import { Images } from '../../Images';
import { Trait, Traits } from '../../Trait';

class Slot {
  public key: Key | undefined

  constructor(
    public readonly id: index,
    public readonly trait: Trait) {
  }
}

export class Panels implements Drawable {
  private slots: Array<Slot | null> = [null, null, null, null, null];
  private coolDownFraction = 1.0;
  private lastRequestedAction?: Trait;
  private slotAnimatedFraction: float = 0.0;
  private animators = new Animators();

  constructor(private readonly images: Images) {
    this.slots[0] = new Slot(0, Traits.melee);
    this.slots[1] = new Slot(1, Traits.fireball);
    this.slots[2] = new Slot(2, Traits.fireshock);
    this.slots[3] = new Slot(3, Traits.shot);
  }

  public onHotKeysUpdate(map: Map<Key, Trait>) {
    this.slots.forEach((slot) => {
      if (slot) {
        map.forEach((trait, key) => {
          if (slot.trait === trait) {
            slot.key = key;
          }
        })
      }
    });
  }

  draw(time: DOMHighResTimeStamp, p: CanvasContext): void {
    this.animators.run(time);
    const ctx = p.ctx;
    const [width, height] = [ctx.canvas.clientWidth, ctx.canvas.clientHeight];

    let x = 60;
    const y = height - 45;
    for (let i = 0; i < 5; i++) {

      x = 60 + 60 * i;

      const slot = this.slots[i];
      if (slot) {
        const slotImg = this.images.get(slot.trait.asset);
        if (slotImg)
          ctx.drawImage(slotImg, 0, 0, slotImg.width, slotImg.height, x - 25, y - 25, 50, 50);

        if (this.coolDownFraction) {
          p.fill(toRGBA("#000", 0.66));
          ctx.beginPath();
          ctx.arc(x, y, 25, 1.5 * Math.PI, (1.5 + this.coolDownFraction * 2) * Math.PI, true);
          ctx.lineTo(x, y);
          ctx.fill();
        }
      } else {
        p.fill(toRGBA("#000", 0.2));
        ctx.beginPath();
        ctx.arc(x, y, 25, 0, 2 * Math.PI);
        ctx.fill();
      }

      if (slot && this.lastRequestedAction === slot.trait && this.slotAnimatedFraction) {
        p.circle(x, y, 23.5, {style: "yellow", width: 4});
      } else {
        p.circle(x, y, 25, {style: "white", width: 2});
      }

      if (slot && slot.key) {

        if (slot.key.asset) {
          const img = this.images.get(slot.key.asset)!!;
          ctx.drawImage(img, 0, 0, 32, 32, x-8, y+20, 16, 16);
        } else {
          p.fillRect(x - 8, y + 17, 16, 16, "#cc0100");
          p.text(slot.key.name, x, y + 18, {align: 'center', font: "bold 12px sans-serif", style: "#fff"})
        }
      }
    }
  }


  activate(action: ActivateTrait) {
    this.lastRequestedAction = action.trait;
    this.animators.set("slot_activate", new Animator(200, f => this.slotAnimatedFraction = f), () => this.slotAnimatedFraction = 0);
    this.animators.set("global_cooldown", new Animator(500, f => this.coolDownFraction = f));
  }
}
