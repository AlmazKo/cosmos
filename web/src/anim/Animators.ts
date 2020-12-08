import { Animated } from './Animator';

export class Animators {
  private animators: { [name: string]: Animated; }         = {};
  private finishCallbacks: { [name: string]: () => void; } = {};

  finish(name: string) {
    if (this.animators[name]) {
      this.animators[name].finish();
      delete this.animators[name];
      // console.debug('finish(1) animator: ', name)
    }
  }

  interrupt(name: string) {
    delete this.animators[name];
    delete this.finishCallbacks[name];
    //  console.debug('interrupt animator: ', name)
  }

  interruptAll() {
    this.animators       = {};
    this.finishCallbacks = {};
  }

  finishAll() {
    for (let key in this.animators) {
      this.animators[key].finish();
    }
  }

  run(time: number) {
    for (let name in this.animators) {
      if (this.animators[name].run(time)) {
        delete this.animators[name];

        const finisher = this.finishCallbacks[name];
        if (finisher) finisher();
        delete this.finishCallbacks[name];
        //     console.debug('Finished animator: ', name)
      }
    }
  }

  has(name: string): boolean {
    return this.animators[name] != null;
  }

  isActive(): boolean {
    return Object.keys(this.animators).length !== 0;
  }

  set(name: string, animator: Animated, onFinish?: () => void) {
    this.animators[name] = animator;
    if (onFinish) {
      this.finishCallbacks[name] = onFinish;
    }
  }
}
