import {CanvasContext} from '../../draw/CanvasContext';

/**
 @deprecated
 */
export interface Drawable {

  draw(time: DOMHighResTimeStamp, bp: CanvasContext): void
}
