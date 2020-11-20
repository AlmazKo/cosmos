import { asset } from './Images';

export interface Trait {
  readonly asset: asset
  readonly  name: string
}

export class TraitMelee implements Trait {
  readonly asset = "ico_melee";
  readonly name = "Melee attack";
}

export class TraitShot implements Trait {
  readonly asset = "ico_shot";
  readonly name = "Shot";
}

export class TraitFireball implements Trait {
  readonly asset = "ico_fireball";
  readonly name = "Cast fireball";
}

export class TraitFireshock implements Trait {
  readonly asset = "ico_fireshock";
  readonly name = "Cast fireshock";
}

export const Traits = {
  melee    : new TraitMelee(),
  fireball : new TraitFireball(),
  shot     : new TraitShot(),
  fireshock: new TraitFireshock(),
};
