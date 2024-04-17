import {TilePainter} from './TilePainter';
import {Camera} from "./Camera";

export interface TileDrawable {
    draw(ime: DOMHighResTimeStamp, bp: TilePainter, camera: Camera): void
}
