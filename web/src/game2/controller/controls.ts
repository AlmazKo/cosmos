import { asset } from '../render/Images';
import { Trait } from '../Trait';
import { keyboardSchema } from './Keyboard';

export class Key {
  constructor(
    public readonly code: uint,
    public readonly name: string,
    public readonly asset: asset | undefined = undefined
  ) {
  }

  toString() {
    return name;
  }
}

export class HotKey {
  constructor(
    public readonly key: Key,
    public readonly trait: Trait) {
  }
}
