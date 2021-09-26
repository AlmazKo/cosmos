import { MapPieceRaw } from '../../game/api/MapPieceRaw';

export interface MapApi {

  getMapPiece(world: string, x: int, y: int): Promise<MapPieceRaw>

  getObjectsPiece(x: int, y: int): Promise<MapPieceRaw>

}
