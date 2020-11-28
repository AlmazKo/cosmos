import { TFireball, TMelee, Trait, TShot } from '../Trait';
import { ActivateTrait } from './actions/ActivateTrait';
import { Player } from './Player';


let ID = 1;

export class Spells {
  private lastTraitTime: tsm = 0;
  private time: tsm = 0;
  private activated = new Map<Trait, ActivateTrait>();


  state(trait: Trait): floatShare {
    const found = this.activated.get(trait);
    if (!found) {
      return Math.min((this.time - this.lastTraitTime) / 500, 1);
    } else {
      return Math.min((this.time - found.startTime) / trait.cooldown, 1);
    }
  }

  onFrame(time: DOMHighResTimeStamp) {
    this.time = time;
    this.activated.forEach((a) => {
      if (time - a.startTime > a.trait.cooldown) {
        this.activated.delete(a.trait);
      }
    })
  }

  onAction(p: Player, trait: Trait): ActivateTrait | undefined {
    const now = this.time;

    if (this.activated.has(trait)) return;

    if (trait instanceof TFireball) {
      if (now - this.lastTraitTime < 1050) return;
    } else if (trait instanceof TMelee) {
      if (now - this.lastTraitTime < 550) return;
    } else if (trait instanceof TShot) {
      if (now - this.lastTraitTime < 1050) return;
    }
    this.lastTraitTime = now;
    const activated = new ActivateTrait(ID++, p, now, trait)
    this.activated.set(trait, activated);
    return activated
  }
}
