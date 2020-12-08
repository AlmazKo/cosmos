import { HtmlListener } from './HtmlListener';
import { listen } from './util';

export class MouseZoom implements HtmlListener {
  private readonly closeCallback: () => any;
  private scale = 1.0;

  constructor(element: HTMLElement, private readonly callback: (zoom: float) => boolean) {
    const wheel = listen(element, 'wheel', (e) => this.onWheel(e));
    this.closeCallback = () => wheel.remove();
  }

  private onWheel(e: WheelEvent) {
    let delta = e.deltaY;
    if (e.deltaMode === 1) {
      // 1 is "lines", 0 is "pixels"
      // Firefox uses "lines" for some types of mouse
      delta *= 15;
    }

    // ctrlKey is true when pinch-zooming on a trackpad.
    const divisor = e.ctrlKey ? 100 : 300;
    const scaleDiff = 1 - delta / divisor;

    let newScale = this.scale * scaleDiff;

    if (newScale < 0.1) {
      newScale = 0.1;
    }
    if (newScale > 20) {
      newScale = 20;
    }

    if (this.callback(newScale) !== false) {
      this.scale = newScale;
    }

    e.preventDefault();
    return false;
  }

  close(): void {
    this.closeCallback();
  }
}
