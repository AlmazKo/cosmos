
export class CanvasHolder {
  canvas: HTMLCanvasElement;
  ctx: CanvasRenderingContext2D;
  width: px;
  height: px;
  ratio: number;

  private readonly watchParent: boolean;

  constructor(
    private readonly container: Element,
    size: [px, px] | undefined,
    readonly sizeCallback: (width: px, height: px) => any,
  ) {
    if (size) {
      this.watchParent = false;
      this.width = size[0];
      this.height = size[1];
    } else {
      const posStyle = window.getComputedStyle(container).getPropertyValue('position');
      if (posStyle !== 'absolute') {
        console.warn(
          'Parent container must have an absolute position, the fixed length will be used',
        );
        this.watchParent = false;
        this.width = 200;
        this.height = 200;
      } else {
        this.watchParent = true;
        this.width = container.clientWidth;
        this.height = container.clientHeight;
      }
    }

    this.ratio = window.devicePixelRatio ? Math.max(window.devicePixelRatio, 1) : 1;
    this.canvas = this.createCanvasElement();
    this.canvas.style.display = 'block';
    container.appendChild(this.canvas);
    const context = <CanvasRenderingContext2D>this.canvas.getContext('2d', {
      alpha: true,
    });
    if (context === null) {
      throw new Error('Fail create context');
    }
    this.ctx = context;
    this.ctx.imageSmoothingEnabled = false;

    this.updateCanvasSize();
  }

  /**
   * @return {HTMLCanvasElement}
   */
  private createCanvasElement(): HTMLCanvasElement {
    const canvas = document.createElement('canvas');
    canvas.style.width = this.width + 'px';
    canvas.style.height = this.height + 'px';
    canvas.style.outline = 'none';
    canvas.style.userSelect = 'none';
    return canvas;
  }

  update() {
    this.detectChangeRatio();
    this.detectChangeSize();
  }

  private updateCanvasSize() {
    this.canvas.style.width = this.width + 'px';
    this.canvas.style.height = this.height + 'px';

    this.canvas.width = this.width * this.ratio;
    this.canvas.height = this.height * this.ratio;
    this.ctx.scale(this.ratio, this.ratio);

    if (this.ratio === 1) {
      console.log(`Canvas size is set: ${this.canvas.width}x${this.canvas.height}px, ratio: 1:1`);
    } else {
      console.log(
        `Canvas size is set: ${this.canvas.width}x${this.canvas.height},`,
        `real size: ${this.width}x${this.height}px`,
        `ratio: 1:${this.ratio}`,
      );
    }
  }

  private detectChangeSize() {
    const [newWidth, newHeight] = this.getActualSize();
    if (this.width === newWidth && this.height === newHeight) {
      return;
    }

    this.width = newWidth;
    this.height = newHeight;
    this.updateCanvasSize();
    this.sizeCallback(this.width, this.height);
  }

  private getActualSize() {
    let newWidth, newHeight;
    if (this.watchParent) {
      newWidth = this.container.clientWidth;
      newHeight = this.container.clientHeight;
    } else {
      newWidth = this.container.clientWidth;
      newHeight = this.container.clientHeight;
    }
    return [newWidth, newHeight];
  }

  private detectChangeRatio() {
    const newRatio = window.devicePixelRatio;
    if (newRatio >= 1 && newRatio !== this.ratio) {
      this.resetScaling();
      this.ratio = newRatio;
      this.updateCanvasSize();
    }
  }

  private resetScaling() {
    this.canvas.width = this.width;
    this.canvas.height = this.height;
    this.ctx.setTransform(1, 0, 0, 1, 0, 0);
  }

  destroy() {
    if (this.canvas.parentNode) {
      this.container.removeChild(this.canvas);
    }
  }

  isAbsent(): boolean {
    return !this.canvas || !this.canvas.parentNode;
  }
}
