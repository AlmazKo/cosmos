// import { BasePainter } from '../draw/BasePainter';
// import { StrokeStyle } from '../draw/StrokeStyleAcceptor';
// import { Creature } from '../game2/engine/Creature';
// import { Orientation } from '../game2/engine/Orientation';
// import { Images } from '../game2/Images';
// import { CELL, coord, Dir } from '../game2/render/constants';
// import { MapPiece } from './api/MapPiece';
// import { Tiles } from './api/Tiles';
// import { Drawable } from './Drawable';
// import { style } from './styles';
// import { toX, toY } from './TilePainter';
//
// export declare var POS_X: coord;
// export declare var POS_Y: coord;
//
// export declare var PROTO_X: coord;
// export declare var PROTO_Y: coord;
// export declare var SHIFT_X: px;
// export declare var SHIFT_Y: px;
//
//

//
// const TILE_SIZE: px    = 32;//fixme remove. take from api
// const TILESET_SIZE: px = 23;//fixme remove. take from api
//
//
// export class Lands implements Drawable {
//   private readonly tiles = new Map<index, Tile>();
//   private readonly basic: Uint16Array;
//   private readonly objects: Uint16Array;
//   private readonly width: uint;
//   private readonly offsetX: int;
//   private readonly offsetY: int;
//   private readonly height: uint;
//
//   public creatures = new Map<uint, Creature>();
//
//   constructor(map: MapPiece, tiles: Tiles, private readonly images: Images) {
//     this.width   = map.width;
//     this.height  = map.height;
//     this.offsetX = map.x;
//     this.offsetY = map.y;
//     this.basic   = new Uint16Array(map.terrain);
//     this.objects = new Uint16Array(map.objects1);
//     this.initTiles(tiles);
//   }
//
//   private initTiles(typedTiles: Tiles) {
//
//     typedTiles.data.forEach(t => {
//       const tileX = t.id % TILESET_SIZE;
//       const tileY = Math.floor(t.id / TILESET_SIZE);
//       const sx    = TILE_SIZE * tileX;
//       const sy    = TILE_SIZE * tileY;
//       this.tiles.set(t.id, new Tile(t.id, t.type, tileX, tileY, sx, sy));
//     });
//
//     this.objects.forEach(tileId => {
//       if (this.tiles.has(tileId)) return;
//       const tileX = tileId % TILESET_SIZE;
//       const tileY = Math.floor(tileId / TILESET_SIZE);
//       const sx    = TILE_SIZE * tileX;
//       const sy    = TILE_SIZE * tileY;
//       this.tiles.set(tileId, new Tile(tileId, null, tileX, tileY, sx, sy));
//     })
//   }
//
//   canMove(from: [index, index], to: [index, index], isFly: boolean = false): boolean {
//     const [x, y] = to;
//     if (!this.isValid(x, y)) return false;
//
//
//     const basicTile = this.basic[this.toIndex(x, y)];
//     if (!basicTile) return false;
//
//
//     let tile = this.tiles.get(basicTile);
//
//     if (!tile) return true;
//
//     if (tile.type === 'WATER' && !isFly) return false;
//     if (tile.type === 'WALL') return false;
//
//
//     tile = this.tiles.get(this.objects[this.toIndex(x, y)]);
//     if (!tile) return true;
//
//     if (tile.type === 'WATER' && !isFly) return false;
//     if (tile.type === 'WALL' && !isFly) return false;
//
//     return true;
//   }
//
//   canStep2(o: Orientation, to: Dir, isFly: boolean = false) {
//     //implements
//   }
//
//   canStep(from: [index, index], to: Dir, isFly: boolean = false): boolean {
//
//     let [x, y] = from;
//     switch (to) {
//       case Dir.NORTH:
//         y--;
//         break;
//       case Dir.SOUTH:
//         y++;
//         break;
//       case Dir.WEST:
//         x--;
//         break;
//       case Dir.EAST:
//         x++;
//         break;
//
//     }
//     const canMv = this.canMove(from, [x, y], isFly);
//     if (!canMv) return false;
//
//     for (const c of this.creatures.values()[Symbol.iterator]()) {
//       if (c.orientation.x === x && c.orientation.y === y) return false;
//     }
//
//     return true;
//   }
//
//   //
//   // getBasic(posX: index, posY: index): Tile | undefined {
//   //   const tileId = this.basic[posX + posY * MAP_WIDTH];
//   //   if (!tileId) return undefined;
//   //
//   //   return this.tiles.get(tileId);
//   // }
//
//
//   // updateFocus(p: TilePainter, proto: DrawableCreature) {
//   //
//   //   POS_X   = proto.positionX - Math.floor(p.width / CELL / 4);
//   //   POS_Y   = proto.positionY - Math.floor(p.height / CELL / 4);
//   //   PROTO_X = proto.positionX;
//   //   PROTO_Y = proto.positionY;
//   //   SHIFT_X = proto.shiftX;
//   //   SHIFT_Y = proto.shiftY;
//   // }
//
//   draw(time: DOMHighResTimeStamp, p: BasePainter) {
//
//     this.basic.forEach((tileId: uint, idx: index) => {
//       if (tileId === 0) return;
//       //todo add filter not draw tiles
//       const posX = idx % this.width + this.offsetX;
//       const posY = Math.floor(idx / this.width) + this.offsetY;
//
//       this.drawTile(p, tileId, toX(posX), toY(posY));
//       // p.text("" + posX + ";" + posY, toX(posX), toY(posY), style.debugText)
//     });
//
//     this.objects.forEach((tileId: uint, idx: index) => {
//       if (tileId === 0) return;
//       const posX = idx % this.width + this.offsetX;
//       const posY = Math.floor(idx / this.width) + this.offsetY;
//       this.drawTile(p, tileId, toX(posX), toY(posY));
//     });
//
//
//     for (let pos = this.offsetX; pos < this.width; pos++) {
//       p.vline(toX(pos), -SHIFT_Y, this.height * CELL, style.grid as StrokeStyle, false);
//     }
//
//     for (let pos = this.offsetY; pos < this.height; pos++) {
//       p.hline(-SHIFT_X, this.width * CELL, toY(pos), style.grid as StrokeStyle, false);
//     }
//   }
//
//   drawTile(p: BasePainter, tileId: uint, x: px, y: px) {
//     if (x < -TILE_SIZE || y < -TILE_SIZE) return;
//     if (x > p.width + TILE_SIZE || y > p.height + TILE_SIZE) return;
//
//     const t = this.tiles.get(tileId);
//     if (!t) return;
//
//     const img = this.images.get('map1');
//     if (!img) return;
//
//     p.ctx.drawImage(img, t.sx, t.sy, TILE_SIZE, TILE_SIZE, x, y, TILE_SIZE, TILE_SIZE);
//   }
//
//   private toIndex(x: int, y: int): uint {
//     return x - this.offsetX + (y - this.offsetY) * this.width;
//   }
//
//   private isValid(x: int, y: int): boolean {
//     return x >= this.offsetX && x < (this.offsetX + this.width) && y >= this.offsetY && y < (this.offsetY + this.height);
//   }
// }
