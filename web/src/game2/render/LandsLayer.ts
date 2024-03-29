import { CanvasContext } from '../../draw/CanvasContext';
import { Layer } from './layers/Layer';
import { Images } from './Images';
import { floor, Piece, World } from '../world/World';
import { Camera } from './Camera';
import { CELL } from './constants';

const ratio = 2;
export const CELL2 = CELL * ratio;
const PIECE_SIZE: px = CELL * 16;//fixme remove. take from api
const PIECE_SIZE2: px = PIECE_SIZE * ratio;
export const TILE_SIZE: px = 32;//fixme remove. take from api
export const TILESET_SIZE: px = 23;//fixme remove. take from api


export class LandsLayer implements Layer {

  // @ts-ignore
  private ctx: CanvasContext;
  private readonly offCtx: CanvasContext;
  private readonly offCanvas: OffscreenCanvas;
  private basicCache = new Map<any, ImageBitmap>();
  private readonly NO_DATA: ImageBitmap;

  constructor(
    private readonly world: World,
    private readonly images: Images,
  ) {

    this.offCanvas = new (window as any).OffscreenCanvas(PIECE_SIZE * ratio, PIECE_SIZE * ratio) as any;
    const offCtx: CanvasRenderingContext2D = this.offCanvas.getContext('2d', {alpha: true}) as any;
    offCtx.imageSmoothingEnabled = false;
    offCtx.imageSmoothingQuality = "high";
    this.offCtx = new CanvasContext(offCtx);


    this.offCtx.fillRect(0, 0, PIECE_SIZE * ratio, PIECE_SIZE * ratio, '#999');
    this.offCtx.rect(0, 0, PIECE_SIZE * ratio, PIECE_SIZE * ratio, '#333');
    this.offCtx.text("NO DATA", PIECE_SIZE, PIECE_SIZE, {style: '#ccc', font: 'bold 60px sans-serif', align: 'center', baseline: 'bottom'});
    this.NO_DATA = this.offCanvas.transferToImageBitmap();
  }

  draw(time: DOMHighResTimeStamp, camera: Camera) {

    const loadRadius = 20;
    this.world.iterateLands(camera.target.x, camera.target.y, loadRadius, piece => {

      if (piece) {
        const img = this.getPieceImage(piece);
        const x = camera.toX(piece.x);
        const y = camera.toY(piece.y);

        if (img) this.ctx.ctx.drawImage(img, 0, 0, PIECE_SIZE * ratio, PIECE_SIZE * ratio, x, y, PIECE_SIZE, PIECE_SIZE);

        // this.ctx.rect(x, y, 1024, 1024, {style: 'black'});

        // this.ctx.text(`${piece.x}x${piece.y}`, x + 2, y + 2, {style: 'red'});

      }
    });
  }

  getPieceImage(piece: Piece): ImageBitmap {
    // return NO_DATA;
    const tileSet = this.images.get('map1');
    if (!tileSet || piece.data.length == 0) return this.NO_DATA;

    let img = this.basicCache.get(piece);

    if (img === undefined) {
      img = this.renderPiece(piece, tileSet);
      if (img === undefined) return this.NO_DATA;
      this.basicCache.set(piece, img);
    }

    return img;
  }

  private renderPiece(piece: Piece, img: HTMLImageElement): ImageBitmap | undefined {
    const ctx = this.offCtx;
    ctx.clear();

    for (let i = 0; i < piece.data.length; i++) {
      const land = piece.data[i];

      const x = (i % 16) * CELL2;
      const y = floor(i / 16) * CELL2;


      const tileX = land.tileId % TILESET_SIZE;
      const tileY = Math.floor(land.tileId / TILESET_SIZE);
      const sx = TILE_SIZE * tileX;
      const sy = TILE_SIZE * tileY;

      //
      ctx.drawImage(img, sx, sy, TILE_SIZE, TILE_SIZE, x, y, CELL2, CELL2);
      //debug ctx.text(debugTile(land.type), x + CELL2 - 2, y, {style: 'black', font: '20px sans-serif', align: 'right'});
    }
    //comment corner
    // todo debug ctx.text(`${piece.x}; ${piece.y}`, 1, 1, {style: 'black', font: '20px sans-serif'});
    // todo debug ctx.text(`${piece.x}; ${piece.y}`, 0, 0, {style: 'white', font: '20px sans-serif'});

    for (let i = 0; i < 16; i++) {
      ctx.line(0, i * CELL2, PIECE_SIZE2, i * CELL2, {style: '#333333', dash: [2, 3], width: 2});
      ctx.line(i * CELL2, 0, i * CELL2, PIECE_SIZE2, {style: '#333333', dash: [2, 3], width: 2});
    }

    return this.offCanvas.transferToImageBitmap();
  }

  changeSize(width: px, height: px): void {
  }

  init(ctx: CanvasContext): void {
    this.ctx = ctx;
  }
}
