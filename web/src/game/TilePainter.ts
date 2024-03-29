import { CanvasContext } from '../draw/CanvasContext';
import { Images } from '../game2/render/Images';
import { coord } from '../game2/render/constants';

export interface Closure {
  (sx: px, sy: px, x: px, y: coord): void;
}

export class TilePainter {
  readonly ctx: CanvasRenderingContext2D;

  width: px;
  height: px;


  constructor( readonly p: CanvasContext,
              private readonly images: Images) {

    this.ctx    = p.ctx;
    this.width  = p.ctx.canvas.width;
    this.height = p.ctx.canvas.height;
  }


  draw(tileSet: string, sx: px, sy: px, sw: px, sh: px, x: px, y: px) {
    const img = this.images.get(tileSet);
    if (!img) return;

    this.ctx.drawImage(img, sx, sy, sw, sh, x, y, sw, sh)
  }

  drawTo(tileSet: string, sx: px, sy: px, sw: px, sh: px, dx: px, dy: px, dw: px, dh: px) {
    const img = this.images.get(tileSet);
    if (!img) return;

    this.ctx.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh)
  }

  // closure(tileSet: string, sx: px, sy: px, sw: px, sh: px): Closure {
  //   return (sx: px, sy: px, x: px, y: coord) => {
  //     const img = this.images.get(tileSet);
  //     if (!img) return;
  //
  //     this.ctx.drawImage(img, sx, sy, sw, sh, x, y, sw, sh)
  //   }
  //
  // }

  //
  //
  // drawCenterTile(img: CanvasImageSource, sx: px, sy: px, sw: px, sh: px, posX: coord, posY: coord) {
  //   const x = posX * CELL + QCELL;
  //   const y = posY * CELL;
  //
  //   this.ctx.drawImage(img, sx, sy, sw, sh, x, y, sw, sh)
  // }
  //
  // toInTile(posX: coord, posY: coord, shiftX: px = 0, shiftY: px = 0): Painter {
  //
  //   this.bp2.currentX = toX(posX) + shiftX;
  //   this.bp2.currentY = toY(posY) + shiftY;
  //   return this.bp2;
  // }
}
