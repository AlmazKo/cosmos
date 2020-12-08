import { ZOOM_Y_AREA_WIDTH, ZoomYChange } from '../CanvasComposer';
import { HtmlListener } from './HtmlListener';
import { DocEvents } from './util';

export class MouseYZoom implements HtmlListener {
  private events: DocEvents;

  constructor(private readonly element: HTMLElement, private readonly callback: ZoomYChange) {
    this.events = new DocEvents(element);
    this.events.listen('mousedown', (e) => this.onStart(e));
    element.style.cursor = 'pointer';
  }

  private onStart(e: MouseEvent) {
    const element = this.element;
    const br = element.getBoundingClientRect();
    const width = br.width;
    const initX = e.clientX - br.left;
    if (width - initX > ZOOM_Y_AREA_WIDTH) return;

    const initY = e.clientY - br.top;
    let lastY = initY;
    let isMoving: boolean = true;
    element.style.cursor = 'ns-resize';

    const move = this.events.listen2(document, 'mousemove', (e) => {
      if (e.buttons === 0) {
        isMoving = false;
        element.style.cursor = 'pointer';
        this.events.remove('mousemove');
        this.events.remove('mouseup');
        return;
      }
      const y = this.getPosition(e)[1];
      const change = 1 + (lastY - y) / 200;
      this.callback.zoomY(change);
      lastY = y;
    });

    this.events.listen(
      'mouseup',
      (e) => {
        if (!isMoving) return;

        move.remove();
        isMoving = false;
        element.style.cursor = 'pointer';

        const y = this.getPosition(e)[1];
        const change = 1 + (lastY - y) / 200;
        this.callback.zoomY(change);
      },
      { once: true },
    );
  }

  getPosition(e: MouseEvent): [px, px] {
    return [
      e.clientX - this.element.getBoundingClientRect().left,
      e.clientY - this.element.getBoundingClientRect().top,
    ];
  }

  close() {
    this.events.removeAll();
  }
}
