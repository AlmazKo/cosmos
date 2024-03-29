import { CanvasComposer, Support } from '../../canvas/CanvasComposer';
import { Render } from './Render';

export class GameCanvas implements CanvasComposer {

  // @ts-ignore
  height: px;
  // @ts-ignore
  width: px;

  // @ts-ignore
  // private p: BasePainter;

  constructor(public readonly render: Render) {

  }

  changeSize(width: px, height: px): void {

    this.render.changeSize(width, height);
  }


  register(): Partial<Support> {
    return {
      cursor: this.render
    };
  }

  destroy(): void {
  }

  init(ctx: CanvasRenderingContext2D, width: px, height: px): void {
    this.width  = width;
    this.height = height;
    this.render.updateContext(ctx, width, height);
  }

  onEndFrame(time: DOMHighResTimeStamp, error?: Error): void {
  }

  onFrame(time: DOMHighResTimeStamp, frameId?: uint): void {
    this.render.onFrame(time);

  }
}
