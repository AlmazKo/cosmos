import { int, px } from '../../chart/types';

type handler<K extends keyof HTMLElementEventMap> = (
  this: HTMLElement,
  ev: HTMLElementEventMap[K],
) => any;

let INC: int = 0;

export interface Removable {
  readonly type: keyof HTMLElementEventMap;
  readonly listener: any;
  readonly id: int;

  remove(): void;
}

export function listen<K extends keyof HTMLElementEventMap>(
  el: DocumentAndElementEventHandlers,
  type: K,
  listener: handler<K>,
  options?: boolean | AddEventListenerOptions,
): Removable {
  el.addEventListener(type, listener, options);
  const id = ++INC;
  console.debug(`Add listener#${id}: ${type}`);
  return {
    id,
    type,
    listener,
    remove: () => {
      el.removeEventListener(type, listener);
      console.debug('Remove listener: ' + type, el);
    },
  };
}

function isOnce(options: boolean | AddEventListenerOptions) {
  return options && typeof options != 'boolean' && options.once;
}

export const Gesture = {
  lock: false,
};

export const getTouchPosition = (
  event: TouchEvent,
  element: HTMLElement = document.body,
): [px, px] => [
  event.changedTouches[0].screenX - element.getBoundingClientRect().left,
  event.changedTouches[0].screenY - element.getBoundingClientRect().top,
];

export class DocEvents {
  private data: Removable[] = [];

  constructor(private readonly defaultEl: DocumentAndElementEventHandlers) {}

  listen2<K extends keyof HTMLElementEventMap>(
    el: DocumentAndElementEventHandlers,
    type: K,
    listener: handler<K>,
    options?: boolean | AddEventListenerOptions,
  ): Removable {
    const id = ++INC;

    const once = isOnce(options);
    if (once) {
      const origin = listener;
      listener = (e) => {
        origin.call(el as HTMLElement, e);
        this.data.removeIf((it) => it.id === id);
        console.debug(
          `Remove listener#${id}(once): ${type}`,
          this.data.map((r) => r.type),
          el,
        );
      };
    }

    const r: Removable = {
      id,
      listener,
      type,
      remove: () => {
        this.data.removeIf((it) => it.id === id);
        el.removeEventListener(type, listener);
        console.debug(
          `Remove listener#${id}(by demand): ${type}`,
          this.data.map((r) => r.type),
          el,
        );
      },
    };

    el.addEventListener(type, r.listener, options);
    this.data.push(r);
    console.debug(`Add listener#${id}${once ? '(once)' : ''}: ${type}`, el);
    return r;
  }

  listen<K extends keyof HTMLElementEventMap>(
    type: K,
    listener: handler<K>,
    options?: boolean | AddEventListenerOptions,
  ): Removable {
    return this.listen2(this.defaultEl, type, listener, options);
  }

  removeAll() {
    this.data.forEach((r) => r.remove());
    this.data = [];
  }

  remove(type: keyof HTMLElementEventMap) {
    this.data.removeIf((r) => {
      if (r.type === type) {
        r.remove();
        return true;
      }

      console.warn('Not found listener', type);
      return false;
    });
  }
}
