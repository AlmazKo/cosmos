import { Shifted, ZOOM_Y_AREA_WIDTH } from '../CanvasComposer';
import { HtmlListener } from './HtmlListener';
import { DocEvents, Gesture, getTouchPosition } from './util';

export class TouchShift implements HtmlListener {
  private events: DocEvents;

  constructor(private readonly element: HTMLElement, private readonly callback: Shifted) {
    this.events = new DocEvents(element);
    this.events.listen('touchstart', (e) => this.onStart(e));
  }

  private onStart(e: TouchEvent) {
    const width = this.element.getBoundingClientRect().width;
    if (width - e.changedTouches[0].clientX < ZOOM_Y_AREA_WIDTH) return;

    const [initX, initY] = getTouchPosition(e);
    let lastShiftX = 0;
    let stopped = false;

    const move = this.events.listen2(document, 'touchmove', (e) => {
      if (stopped) return;
      if (Gesture.lock) {
        stopped = true;
        this.events.remove('touchmove');
        this.events.remove('touchend');
        this.callback.shiftChange(0, 0, true);
        return;
      }

      const [x, y] = getTouchPosition(e);
      const shiftX = x - initX;
      const changeX = shiftX - lastShiftX;
      this.callback.shiftChange(changeX, 0);
      lastShiftX = shiftX;
      e.preventDefault(); //for preventing selection
      e.stopPropagation();
    });

    let isMoving = true;

    this.events.listen2(
      document,
      'touchend',
      (e) => {
        if (!isMoving || Gesture.lock || stopped) return;

        move.remove();
        isMoving = false;
        const [x, y] = getTouchPosition(e);
        const shiftX = x - initX;
        const shiftY = y - initY;
        if (shiftX !== 0) {
          const changeX = shiftX - lastShiftX;
          this.callback.shiftChange(changeX, 0, true);
          lastShiftX = shiftX;
        }
      },
      { passive: true, once: true },
    );
  }

  close(): void {
    this.events.removeAll();
  }
}
