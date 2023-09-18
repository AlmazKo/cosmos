import {TilePainter} from './TilePainter';
import {Camera} from "../game2/render/Camera";

export interface TileDrawable {
    draw(ime: DOMHighResTimeStamp, bp: TilePainter, camera: Camera): void
}
