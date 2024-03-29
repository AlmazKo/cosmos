import { Dir, TileType } from '../constants';
import { Creature } from '../engine/Creature';
import { MapApi } from '../server/MapApi';
import { Loading } from '../server/util';
import { Land } from './Land';


/**
 * Map position
 */
declare type piecePos = int;
export const floor = Math.floor;

const PIECE_SIZE = 16;


export class Piece {
  constructor(
    readonly x: pos,
    readonly y: pos,
    readonly data: Land[]) {
  }
}

export class World {

  private pieces: Array<Array<Piece | Loading>> = [[]];
  name: string = '';


  constructor(private readonly api: MapApi) {

  }

  iterateLands(posX: pos, posY: pos, radius: uint, handler: (p: Piece | undefined) => void) {
    if(this.name == '') return;

    const fromX = floor((posX - radius) / PIECE_SIZE);
    const fromY = floor((posY - radius) / PIECE_SIZE);

    const toX = floor((posX + radius) / PIECE_SIZE);
    const toY = floor((posY + radius) / PIECE_SIZE);

    for (let x = fromX; x <= toX; x++) {
      for (let y = fromY; y <= toY; y++) {
        handler(this.getPiece(x, y))
      }
    }
  }


  getPiece(x: piecePos, y: piecePos): Piece | undefined {
    let r = this.pieces[x];
    if (r == undefined) {
      r = [];
      this.pieces[x] = r;

    }
    const p = r[y];

    if (p instanceof Piece) {
      return p;
    } else {
      if (p === undefined) {
        r[y] = Loading.REQUESTING;
        this.loadPiece(this.name, x, y)
          .then(i => r[y] = i)
          .catch(() => r[y] = Loading.FAIL)
      }
      return undefined;
    }
  }

  get(x: pos, y: pos): Land | undefined {
    const pX = floor(x / PIECE_SIZE);
    const pY = floor(y / PIECE_SIZE);

    const p = this.getPiece(pX, pY);
    if (p === undefined) return undefined;

    let offsetX = x % PIECE_SIZE;
    let offsetY = y % PIECE_SIZE;
    if (offsetY < 0) {
      offsetY += PIECE_SIZE
    }
    if (offsetX < 0) {
      offsetX += PIECE_SIZE
    }

    return p.data[offsetY * PIECE_SIZE + offsetX];
  }

  loadPiece(mapName: string, x: piecePos, y: piecePos): Promise<Piece> {

    return this.api.getMapPiece(mapName, x, y).map(p => {
      const lands = [];
      let pair: [uint, TileType];

      for (let i = 0; i < p.length; i++) {
        pair = p[i];
        lands[i] = new Land(pair[0], pair[1], x * 16 + i % 16, y * 16 + floor(i / 16))
      }

      return new Piece(x * 16, y * 16, lands)
    })
  }


  tileType(x: pos, y: pos): TileType {
    const land = this.get(x, y);
    if (!land) return TileType.NOTHING;
    return land.type;
  }

  nextTile(fromX: pos, fromY: pos, dir: Dir | null): TileType {
    let x = fromX, y = fromY;

    if (dir === Dir.NORTH) {
      y--;
    } else if (dir === Dir.SOUTH) {
      y++;
    } else if (dir === Dir.EAST) {
      x++;
    } else if (dir === Dir.WEST) {
      x--;
    }

    return this.tileType(x, y);
  }

  canStep(fromX: pos, fromY: pos, dir: Dir | null) {
    const type = this.nextTile(fromX, fromY, dir);
    return type !== TileType.DEEP_WATER && type !== TileType.NOTHING && type !== TileType.WALL;
  }

  canSeeThrough(x: pos, y: pos, dir: Dir | null = null) {
    const type = this.tileType(x, y);
    return type !== TileType.NOTHING && type !== TileType.WALL;
  }

  moveCreature(cr: Creature, toX: pos, toY: pos) {
    cr.orientation.setPosition(toX, toY)
  }
}
