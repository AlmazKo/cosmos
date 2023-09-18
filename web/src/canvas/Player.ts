import { CanvasComposer, Support } from './CanvasComposer';
import { CanvasHolder } from './CanvasHolder';
import { HtmlListener } from './listener/HtmlListener';
import { MouseYZoom } from './listener/MouseYZoom';
import { MouseZoomChange } from './listener/MouseZoomChange';
import { PositionChange } from './listener/PositionChange';
import { TouchPositionChange } from './listener/TouchPositionChange';
import { TouchShift } from './listener/TouchShift';
import { TouchZoomChange } from './listener/TouchZoomChange';
import { isTouchDevice } from './utils';

class FpsMeter {
  times = 0;
  prev  = 0;
  value = 0;

  update(now: number) {
    this.times++;
    if (now - this.prev > 1000) {
      this.value = this.times;
      this.times = 0;
      this.prev  = now;
    }
  }
}

let DEV            = true; //process.env.NODE_ENV === 'development';
let logMsg: string = '';
let logMsg2        = '';
let INC: uint      = 0;

export const log2 = (msg: any) => (logMsg2 = msg);
export const log  = (msg: any) => (logMsg = msg);

export class Player {
  private readonly maxFps: number;
  private readonly canvas: CanvasHolder;

  private started   = false;
  private stopped   = false;
  private destroyed = false;

  // @ts-ignore
  private composer: CanvasComposer;
  private id: number;
  private listeners: HtmlListener[] = [];

  constructor(container: HTMLElement, size?: [px, px], maxFps: number = 120) {
    this.maxFps = maxFps;
    this.canvas = new CanvasHolder(container, size, (w, h) => this.onSizeChanged(w, h));
    this.id     = ++INC;
  }

  start(composer: CanvasComposer) {
    this.composer = composer;
    if (composer.register) {
      this.registerEvents(composer.register());
    }
    let frameId   = 0;
    const fps     = new FpsMeter();
    const minStep = 1000 / this.maxFps;
    let prev      = 0;

    const callback = (now: DOMHighResTimeStamp) => {
      if (this.destroyed) return;

      if (this.canvas.isAbsent()) {
        this.destroy();
        return;
      }

      //need for preventing calling previous callbacks
      //FIXME if (this.id !== this.inc) return;

      if (this.stopped || now - prev < minStep) {
        window.requestAnimationFrame(callback);
        return;
      }

      fps.update(now);
      this.canvas.update();

      if (!this.started) {
        this.started = true;
        composer.init(this.canvas.ctx, this.canvas.width, this.canvas.height);
        console.debug('Init canvas');
      }

      prev = now;

      try {
        composer.onFrame(now, frameId++);
        this.tryCallEndFrame(now);
      } catch (e) {
        console.error('Fail render the frame:', e);
        // if (e == STOP_RENDER) return;
        return;
        //   this.tryCallEndFrame(now, e);
      }

      if (DEV) {
        this.debugInfo(fps);
      }
      window.requestAnimationFrame(callback);
    };

    window.requestAnimationFrame(callback);
  }

  private onSizeChanged(width: px, height: px) {
    if (!this.destroyed && this.composer && this.started) {
      this.composer.changeSize(width, height);
    }
  }

  private debugInfo(fps: FpsMeter) {
    const {ctx, width, height} = this.canvas;
    ctx.font                   = 'bold 16px sans-serif';
    ctx.fillStyle              = '#0f0';
    ctx.textAlign              = 'right';
    ctx.textBaseline           = 'top';
    ctx.strokeStyle            = '#000';
    ctx.lineWidth              = 1;
    ctx.setLineDash([]);
    ctx.strokeText(fps.value + '', width - 2, 0);
    ctx.fillText(fps.value + '', width - 2, 0);
    ctx.fillText(logMsg + '', width - 2, 30);
    ctx.fillText(logMsg2 + '', width - 2, 45);

    //corners
    ctx.strokeRect(0.5, 0.5, 3, 3);
    ctx.strokeRect(width - 3.5, 0.5, 3, 3);
    ctx.strokeRect(0.5, height - 3.5, 3, 3);
    ctx.strokeRect(width - 3.5, height - 3.5, 3, 3);
  }

  private tryCallEndFrame(now: DOMHighResTimeStamp, renderError?: Error) {
    if (!this.composer.onEndFrame) {
      return;
    }

    try {
      this.composer.onEndFrame(now, renderError);
    } catch (e) {
      console.error('Fail render the end of frame', e);
    }
  }

  stop() {
    if (this.stopped || this.destroyed) {
      return;
    }

    this.stopped = true;
    console.log('Appear');
  }

  destroy() {
    if (this.destroyed) {
      return;
    }
    this.stopped   = false;
    this.destroyed = true;
    if (this.composer && typeof this.composer.destroy === 'function') {
      this.composer.destroy();
    }
    this.canvas.destroy();
    this.listeners.forEach((it) => it.close());

    console.log('Destroyed.');
  }

  private registerEvents(listeners: Partial<Support>) {
    if (DEV) {
      window.addEventListener('keypress', (e) => {
        if (e.keyCode == 32) {
          this.stopped = !this.stopped;
          console.log('Stop world');
        }
      });
    }

    const canvas = this.canvas.canvas;
    if (isTouchDevice()) {
      this.registerTouchEvents(listeners, canvas);
    } else {
      this.registerMouseEvents(listeners, canvas);
    }
  }

  private registerTouchEvents(listeners: Partial<Support>, canvas: HTMLElement) {
    if (listeners.zoom) {
      const l = new TouchZoomChange(canvas, (z, pos) => {
        return listeners.zoom!.zoomChange(z, pos);
      });
      this.listeners.push(l);
    }

    if (listeners.shift) {
      const l = new TouchShift(canvas, listeners.shift);
      this.listeners.push(l);
    }

    if (listeners.cursor) {
      const l = new TouchPositionChange(canvas, (pos) =>
        listeners.cursor!.changeCursorPosition(pos),
      );
      this.listeners.push(l);
    }
  }

  private registerMouseEvents(listeners: Partial<Support>, canvas: HTMLElement) {
    if (listeners.zoom) {
      const l = new MouseZoomChange(canvas, listeners.zoom);
      this.listeners.push(l);
    }

    if (listeners.zoomY) {
      const l = new MouseYZoom(canvas, listeners.zoomY);
      this.listeners.push(l);
    }

    if (listeners.cursor) {
      const l = new PositionChange(canvas, (pos) => listeners.cursor!.changeCursorPosition(pos));
      this.listeners.push(l);
    }

  }
}
