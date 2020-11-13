import { Dir } from '../constants';
import { Orientation } from './Orientation';


export class Util {

  public static nextX(o: Orientation): pos {
    if (o.move === Dir.EAST) {
      return o.x + 1;
    } else if (o.move === Dir.WEST) {
      return o.x - 1;
    }

    return o.x;
  }


  public static nextY(o: Orientation): pos {
    if (o.move === Dir.NORTH) {
      return o.y - 1;
    } else if (o.move === Dir.SOUTH) {
      return o.y + 1
    }
    return o.y;
  }


}
