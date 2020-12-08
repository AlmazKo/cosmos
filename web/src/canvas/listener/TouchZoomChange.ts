import { log } from '../Player';
import { HtmlListener } from './HtmlListener';
import { DocEvents, Gesture } from './util';

interface TouchEvent {
  readonly id: number;
  x: px;
  y: px;
}

type pointerEventName =
  | 'pointerdown'
  | 'pointermove'
  | 'pointerup'
  | 'pointercancel'
  | 'pointerout'
  | 'pointerleave';

export class TouchZoomChange implements HtmlListener {
  private first: TouchEvent | undefined;
  private second: TouchEvent | undefined;

  private firstPos: [px, px] | undefined;
  private secondPos: [px, px] | undefined;
  private midPos: [px, px] | undefined;
  private currentScale: float = 1.0;
  private scale: float = 1.0;
  private initDistance: px = 0;
  private events: DocEvents;

  constructor(
    element: HTMLElement,
    private readonly callback: (zoom: float, pos: [px, px]) => any,
  ) {
    this.events = new DocEvents(element);
    this.events.listen('pointerdown', (e) => this.onNew(e));
    this.events.listen('pointermove', (e) => this.moveHandler(e));

    (['pointerup', 'pointercancel', 'pointerout', 'pointerleave'] as pointerEventName[]).forEach(
      (name) => {
        this.events.listen(name, (e) => this.outHandler(e));
      },
    );
  }

  private onNew(e: PointerEvent) {
    const { pointerId, clientX, clientY } = e;

    if (this.first) {
      this.second = {
        id: pointerId,
        x: clientX,
        y: clientY,
      };
    } else {
      this.first = {
        id: pointerId,
        x: clientX,
        y: clientY,
      };
    }
  }

  private update(e: PointerEvent): boolean {
    const { first, second } = this;
    const { pointerId, clientX, clientY } = e;
    // console.log("touch", { pointerId, clientX, clientY })

    if (first && first.id == pointerId) {
      first.x = clientX;
      first.y = clientY;
      return !!second;
    } else if (second && second.id == pointerId) {
      second.x = clientX;
      second.y = clientY;
      return !first;
    }

    return false;
  }

  private moveHandler(ev: PointerEvent) {
    if (!this.update(ev)) return;

    const f = this.first!;
    const s = this.second!;
    const distance = Math.hypot(f.x - s.x, f.y - s.y);

    if (this.midPos === undefined) {
      Gesture.lock = true;

      this.firstPos = [f.x, f.y];
      this.secondPos = [s.x, s.y];
      this.midPos = [(f.x + s.x) / 2, (f.y + s.y) / 2];
      this.initDistance = distance;
      this.currentScale = 1.0;
    } else {
      const scaleDiff = distance / this.initDistance;
      let newScale = this.scale * scaleDiff;

      if (this.scale < 0.1) {
        newScale = 0.1;
      }
      if (this.scale > 20) {
        newScale = 20;
      }

      log(this.midPos[0]);

      console.log(newScale / this.currentScale);
      this.callback(newScale / this.currentScale, this.midPos!);
      this.currentScale = newScale;
    }
  }

  private outHandler(e: PointerEvent) {
    if (this.first && this.first.id == e.pointerId) {
      this.first = undefined;
      this.clear();
    } else if (this.second && this.second.id == e.pointerId) {
      this.second = undefined;
      this.clear();
    }
  }

  private clear() {
    Gesture.lock = false;
    this.scale = this.currentScale;
    this.firstPos = undefined;
    this.secondPos = undefined;
    this.midPos = undefined;
  }

  close() {
    this.clear();
    this.events.removeAll();
  }
}
