import { CanvasComposer } from '../../../canvas/CanvasComposer';
import { BasePainter } from '../../../draw/BasePainter';
import { stringTiles } from '../../constants';
import { Player } from '../../engine/Player';
import { Images } from '../Images';
import { TILE_SIZE, TILESET_SIZE } from '../LandsLayer';
import { Render } from '../Render';

export class TileInfoCanvas implements CanvasComposer {
  // @ts-ignore
  height: px;
  // @ts-ignore
  width: px;
  // @ts-ignore
  private p: BasePainter;
  private nameDiv: HTMLElement;


  constructor(
    private readonly render: Render,
    private readonly images: Images
  ) {
    this.nameDiv = document.getElementById("tileinfo_name")!!;
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

    const tileSet = this.images.get('map1');
    if (!tileSet) return;


    //todo add cache
    const {x, y} = proto.orientation;
    const land = this.render.game.world.get(x, y)!!;
    const {sx, sy} = this.getTileCoord(land.tileId);

    this.p.ctx.imageSmoothingEnabled = false;
    this.p.ctx.drawImage(tileSet, sx, sy, TILE_SIZE, TILE_SIZE, 0, 0, this.width, this.height);

    const tileObjId = this.findObject(proto);
    if (tileObjId !== null) {
      const {sx, sy} = this.getTileCoord(tileObjId);
      this.p.ctx.drawImage(tileSet, sx, sy, TILE_SIZE, TILE_SIZE, 0, 0, this.width, this.height);
    }

    this.nameDiv.innerText = stringTiles[land.type].toLowerCase()
  }

  private getTileCoord(tileId: index) {
    const tileX = tileId % TILESET_SIZE;
    const tileY = Math.floor(tileId / TILESET_SIZE);
    const sx = TILE_SIZE * tileX;
    const sy = TILE_SIZE * tileY;
    return {sx, sy};
  }

  findObject(proto: Player): index | null {
    const {x, y} = proto.orientation;


    for (let [_, v] of proto.zoneObjects) {
      if (v.x === x && v.y === y) {
        return v.tileId
      }
    }
    return null;
  }

  onEndFrame(time: DOMHighResTimeStamp, error?: Error): void {
  }

  destroy(): void {
  }
}
