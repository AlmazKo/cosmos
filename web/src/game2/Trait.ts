import { asset } from './render/Images';

export interface Trait {
  readonly ico: asset
  readonly audio?: asset
  readonly name: string
  readonly castTime: secm;
  readonly cooldown: secm;
}

export class TMelee implements Trait {
  readonly ico = "ico_melee";
  readonly name = "Melee attack";
  readonly castTime = 0;
  readonly cooldown = 500;
  readonly audio = "melee.wav";
}

export class TShot implements Trait {
  readonly ico = "ico_shot";
  readonly name = "Shot";
  readonly castTime = 0;
  readonly cooldown = 2000;
}

export class TFireball implements Trait {
  readonly ico = "ico_fireball";
  readonly name = "Cast fireball";
  readonly audio = "fireball.wav";
  readonly castTime = 0;
  readonly cooldown = 1000;
}

export class TFireshock implements Trait {
  readonly ico = "ico_fireshock";
  readonly name = "Cast fireshock";
  readonly castTime = 0;
  readonly cooldown = 15000;
}

export const Traits = {
  melee    : new TMelee(),
  fireball : new TFireball(),
  shot     : new TShot(),
  fireshock: new TFireshock(),
};
