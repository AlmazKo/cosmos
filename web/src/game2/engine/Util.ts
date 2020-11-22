import { Dir } from '../constants';
import { Orientation } from './Orientation';


export class Util {

  public static nextX(o: Orientation, dir: Dir | null = o.move): pos {
    if (dir === Dir.EAST) {
      return o.x + 1;
    } else if (dir === Dir.WEST) {
      return o.x - 1;
    }

    return o.x;
  }


  public static nextY(o: Orientation, dir: Dir | null = o.move): pos {
    if (dir === Dir.NORTH) {
      return o.y - 1;
    } else if (dir === Dir.SOUTH) {
      return o.y + 1
    }
    return o.y;
  }


}
