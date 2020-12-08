import { HtmlListener } from './HtmlListener';
import { DocEvents } from './util';

export class PositionChange implements HtmlListener {
  private events: DocEvents;
  private cursorPosition: [px, px] | null = null;

  constructor(
    private readonly element: HTMLElement,
    private readonly callback: (pos?: [px, px]) => any,
  ) {
    this.events = new DocEvents(element);

    this.events.listen('mousemove', (e) => this.setCursorPosition(e), { passive: true });
    this.events.listen('mouseout', () => this.setCursorPosition(), { passive: true });
  }

  private setCursorPosition(e?: MouseEvent) {
    if (e) {
      this.cursorPosition = this.getPosition(e);
      this.callback(this.cursorPosition);
    } else {
      this.cursorPosition = null;
      this.callback();
    }
  }

  getPosition(e: MouseEvent): [px, px] {
    return [
      Math.round(e.clientX - this.element.getBoundingClientRect().left),
      Math.round(e.clientY - this.element.getBoundingClientRect().top),
    ];
  }

  close(): void {
    this.events.removeAll();
  }
}
