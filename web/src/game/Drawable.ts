import { BasePainter } from '../draw/BasePainter';
import { CanvasContext } from '../draw/CanvasContext';

/**
 @deprecated
 */
export interface Drawable {

  draw(time: DOMHighResTimeStamp, bp: CanvasContext): void
}
