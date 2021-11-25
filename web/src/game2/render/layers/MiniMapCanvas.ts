import { CanvasComposer } from '../../../canvas/CanvasComposer';
import { BasePainter } from '../../../draw/BasePainter';
import { style } from '../../../game/styles';
import { TileType } from '../../constants';
import { Land } from '../../world/Land';
import { floor } from '../../world/World';
import { Render } from '../Render';

export const getColor = (type: TileType) => {
  switch (type) {
    case TileType.GRASS:
      return '#8bd742'
    case TileType.SHALLOW:
      return '#63e8ff'
    case TileType.DEEP_WATER:
      return '#0099f9'
    case TileType.GATE:
      return '#ffcf73'
    case TileType.TIMBER:
      return '#cb972a'
    case TileType.SAND:
      return '#ffea66'
    case TileType.WALL:
      return '#4d5d46'
  }
  return '#999';
};

export class MiniMapCanvas implements CanvasComposer {
  // @ts-ignore
  height: px;
  // @ts-ignore
  width: px;
  // @ts-ignore
  private p: BasePainter;



  constructor(private readonly render: Render) {
  }

  init(ctx: CanvasRenderingContext2D, width: px, height: px): void {
    this.width = width;
    this.height = height;
    this.p = new BasePainter(ctx);
  }

  changeSize(width: px, height: px): void {
    this.width = width;
    this.height = height;
  }

  onFrame(time: DOMHighResTimeStamp, frameId: uint): void {
    this.p.clearArea(this.width, this.height);
    this.draw(time);
  }

  private draw(time: DOMHighResTimeStamp) {
    const proto = this.render.game.getProto()
    if (!proto) return;

    const p = this.p;
    const pixel = 3;
    const loadRadius = Math.min(32, this.width / (pixel * 2));
    const cameraX = this.render.camera.target.x;
    const cameraY = this.render.camera.target.y;
    const lx = cameraX - loadRadius;
    const ty = cameraY - loadRadius;

    this.render.game.world.iterateLands(cameraX, cameraY, loadRadius, piece => {
      if (!piece) return;

      for (let i = 0; i < piece.data.length; i++) {
        const land = piece.data[i];
        const x: pos = piece.x + (i % 16);
        const y: pos = piece.y + floor(i / 16);
        let color = getColor(land.type);
        p.fillRect((x - lx) * pixel, (y - ty) * pixel, pixel, pixel, color)
      }

      proto.zoneCreatures.forEach((cr) => {
        p.fillRect((cr.orientation.x - lx) * pixel, (cr.orientation.y - ty) * pixel, pixel, pixel, 'red')
      })

      if (time % 1000 < 500) {
        const centerX = cameraX - lx;
        const centerY = cameraY - ty;
        p.fillRect(centerX * pixel, centerY * pixel, pixel, pixel, '#80ff80')
        p.rect(centerX * pixel, centerY * pixel, pixel, pixel, 'black', true)
      }
    });

    const txt = proto.x() + "; " + proto.y();
    p.ctx.shadowBlur = 3;
    p.ctx.shadowColor = '#000';
    p.text(txt, this.width - 2, this.height - 1, style.minimapDetails)
    p.ctx.shadowBlur = 0;
    p.ctx.shadowColor = '#00000000';
  }

  onEndFrame(time: DOMHighResTimeStamp, error?: Error): void {
  }

  destroy(): void {
  }
}
