import { asset } from './render/Images';

export interface Trait {
  readonly ico: asset
  readonly audio?: asset
  readonly name: string
}

export class TraitMelee implements Trait {
  readonly ico = "ico_melee";
  readonly name = "Melee attack";
  readonly audio = "melee.wav";
}

export class TraitShot implements Trait {
  readonly ico = "ico_shot";
  readonly name = "Shot";
}

export class TraitFireball implements Trait {
  readonly ico = "ico_fireball";
  readonly name = "Cast fireball";
  readonly audio = "fireball.wav";
}

export class TraitFireshock implements Trait {
  readonly ico = "ico_fireshock";
  readonly name = "Cast fireshock";
}

export const Traits = {
  melee    : new TraitMelee(),
  fireball : new TraitFireball(),
  shot     : new TraitShot(),
  fireshock: new TraitFireshock(),
};
