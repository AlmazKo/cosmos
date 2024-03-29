import { hround, round } from '../canvas/utils';
import { FillStyle } from './FillStyle';
import { FontStyle, FontStyleAcceptor } from './FontStyleAcceptor';
import { Painter, StringStokeStyle } from './Painter';
import { StrokeStyleAcceptor } from './StrokeStyleAcceptor';

export class BasePainter implements Painter {
  readonly ctx: CanvasRenderingContext2D;
  private readonly strokeAcceptor: StrokeStyleAcceptor;
  private readonly fontAcceptor: FontStyleAcceptor;
  private readonly baseFillColor: FillStyle;

  private hMeasuringCache: {
    [key: string]: px | undefined;
  } = {};
  /** @deprecated use other ways - it's dynamic values*/
  width: number;
  /** @deprecated use other ways - it's dynamic values*/
  height: number;

  constructor(
    ctx: CanvasRenderingContext2D,
    baseColor: color     = '#000',
    baseFillColor: color = '#fff',
  ) {
    this.ctx            = ctx;
    this.baseFillColor  = baseFillColor;
    this.strokeAcceptor = new StrokeStyleAcceptor(ctx, baseColor);
    this.fontAcceptor   = new FontStyleAcceptor(ctx, baseColor);
    this.width          = ctx.canvas.width;
    this.height         = ctx.canvas.height;
  }

  /**
   * Draw a vertical line
   */
  vline(x: px, y1: px, y2: px, style: StringStokeStyle, pixelPerfect = true) {
    this.stroke(style);
    if (pixelPerfect && this.ctx.lineWidth % 2 === 1) {
      x = hround(x);
    }

    this.ctx.beginPath();
    this.ctx.moveTo(x, y1);
    this.ctx.lineTo(x, y2);
    this.ctx.stroke();
  }

  /**
   * Draw a horizontal line
   */
  hline(x1: px, x2: px, y: px, style: StringStokeStyle, pixelPerfect = true) {
    this.stroke(style);
    if (pixelPerfect && this.ctx.lineWidth % 2 === 1) {
      y = hround(y);
    }

    this.ctx.beginPath();
    this.ctx.moveTo(x1, y);
    this.ctx.lineTo(x2, y);
    this.ctx.stroke();
  }

  /**
   * Draw a line
   */
  line(x1: px, y1: px, x2: px, y2: px, style: StringStokeStyle) {
    this.stroke(style);

    this.ctx.beginPath();
    this.ctx.moveTo(x1, y1);
    this.ctx.lineTo(x2, y2);

    this.ctx.stroke();
  }

  /**
   * Draw a stroke rectangle
   */
  rect(x: px, y: px, w: px, h: px, style: StringStokeStyle, pixelPerfect = true) {
    this.stroke(style);
    if (pixelPerfect && this.ctx.lineWidth % 2 === 1) {
      x = hround(x);
      y = hround(y);
    }
    this.ctx.strokeRect(x, y, w, h);
  }

  fillRect(x: px, y: px, w: px, h: px, style?: FillStyle, pixelPerfect = true) {
    this.fill(style);

    if (pixelPerfect) {
      this.ctx.fillRect(round(x), round(y), round(w), round(h));
    } else {
      this.ctx.fillRect(x, y, w, h);
    }
  }

  roundRect(x: px, y: px, w: px, h: px, style: StringStokeStyle, radius: px) {
    this.stroke(style);
    this.roundRectPath(x, y, w, h, radius);
    this.ctx.stroke();
  }

  roundFillRect(x: px, y: px, w: px, h: px, style: FillStyle, radius: px) {
    this.fill(style);
    this.roundRectPath(x, y, w, h, radius);
    this.ctx.fill();
  }

  private roundRectPath(x: px, y: px, w: px, h: px, radius: px | [px, px, px, px]) {
    const r   = typeof radius === 'number' ? [radius, radius, radius, radius] : radius;
    const ctx = this.ctx;
    const p   = ctx.beginPath();
    ctx.moveTo(x + r[0], y);
    ctx.lineTo(x + w - r[1], y);
    ctx.quadraticCurveTo(x + w, y, x + w, y + r[1]);
    ctx.lineTo(x + w, y + h - r[2]);
    ctx.quadraticCurveTo(x + w, y + h, x + w - r[2], y + h);
    ctx.lineTo(x + r[3], y + h);
    ctx.quadraticCurveTo(x, y + h, x, y + h - r[3]);
    ctx.lineTo(x, y + r[0]);
    ctx.quadraticCurveTo(x, y, x + r[0], y);
    ctx.closePath();
    return p;
  }

  fillCircle(x: px, y: px, radius: px, style?: FillStyle) {
    if (radius < 0.5) radius = 0.5;
    this.fill(style);
    this.ctx.beginPath();
    this.ctx.arc(x, y, radius, 0, 2 * Math.PI);
    this.ctx.fill();
  }

  circle(x: px, y: px, radius: px, style: StringStokeStyle) {
    if (radius < 0.5) radius = 0.5;
    this.stroke(style);
    this.ctx.beginPath();
    this.ctx.arc(x, y, radius, 0, 2 * Math.PI);
    this.ctx.stroke();
  }

  text(text: string, x: px, y: px, style?: Partial<FontStyle>, maxWidth?: px) {
    this.fontAcceptor.set(style);
    /* IE doesn't work without max width param */
    this.ctx.fillText(text, x, y, maxWidth || 1000);
  }

  debug(text: any, x: px, y: px) {
    this.text('' + text, x, y);
  }

  clearArea(width: px, height: px) {
    this.ctx.clearRect(0, 0, width, height);
  }

  measureHeight(style: FontStyle): px {
    const styleHash    = JSON.stringify(style);
    const cachedHeight = this.hMeasuringCache[styleHash];

    if (cachedHeight) {
      return cachedHeight;
    }

    const span = document.createElement('span');

    /* geometry */
    span.style.position = 'absolute';
    span.style.top      = '100px';
    span.style.left     = '100px';

    /* font style */
    span.style.color = style.style!;
    span.style.font  = style.font!;

    span.appendChild(document.createTextNode('Аy'));
    document.body.appendChild(span);

    const h = parseInt(window.getComputedStyle(span).height || '');

    document.body.removeChild(span);

    this.hMeasuringCache[styleHash] = h;

    return h;
  }

  measureWidth(text: string, style: Partial<FontStyle>): px {
    this.fontAcceptor.set(style);
    return this.ctx.measureText(text).width;
  }

  stroke(strokeStyle: Partial<StringStokeStyle>) {
    if (!strokeStyle) {
      console.warn('Try to set empty style');
    }

    this.strokeAcceptor.set(strokeStyle);
  }

  fill(style?: FillStyle) {
    if (style) this.ctx.fillStyle = style;
  }

  font(style?: FontStyle) {
    this.fontAcceptor.set(style);
  }

  beginPath(startX: px, startY: px): CanvasPath {
    this.ctx.beginPath();
    this.ctx.moveTo(startX, startY);
    return this.ctx;
  }

  closePath(strokeStyle?: Partial<StringStokeStyle>, style?: FillStyle) {
    if (strokeStyle) {
      this.closeStrokePath(strokeStyle);
    }
    if (style) {
      this.closeFillPath(style);
    }
  }

  closeFillPath(style: FillStyle) {
    this.ctx.fillStyle = style;
    this.ctx.closePath();
    this.ctx.fill();
  }

  beginPath0(): CanvasPath {
    this.ctx.beginPath();
    return this.ctx;
  }

  closeStrokePath(strokeStyle: Partial<StringStokeStyle>): void {
    this.stroke(strokeStyle);
    this.ctx.stroke();
  }

  ellipse(
    x: px,
    y: px,
    radiusX: px,
    radiusY: px,
    rotation: number,
    startAngle: number,
    endAngle: number,
    anticlockwise?: boolean,
  ): void {
  }

  vGradient(y1: px, y2: px, levels: Array<[number, color]>): CanvasGradient {
    const gr = this.ctx.createLinearGradient(0, y1, 0, y2);

    levels.forEach(([offset, color]) => {
      gr.addColorStop(offset, color);
    });
    return gr;
  }
}
