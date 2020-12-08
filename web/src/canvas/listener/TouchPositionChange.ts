import { HtmlListener } from './HtmlListener';
import { DocEvents, getTouchPosition } from './util';

export class TouchPositionChange implements HtmlListener {
  private events: DocEvents;

  constructor(
    private readonly element: HTMLElement,
    private readonly callback: (pos?: [px, px]) => any,
  ) {
    this.events = new DocEvents(element);
    this.events.listen2(document, 'touchstart', (e) => this.onStart(e));
  }

  private onStart(e: TouchEvent) {
    if (e.target != this.element) {
      this.callback();
      return;
    }

    const [startX, y] = getTouchPosition(e, this.element);

    const move = this.events.listen2(document, 'touchmove', (e) => {
      const [x, y] = getTouchPosition(e, this.element);
      this.callback([x, y]);
      if (Math.abs(startX - x) > 40) {
        //detect strong horizontal moving
        e.preventDefault();
      }
    });

    this.events.listen2(
      document,
      'touchend',
      () => {
        move.remove();
      },
      { passive: true, once: true },
    );

    this.callback([startX, y]);
  }

  close(): void {
    this.events.removeAll();
  }
}
