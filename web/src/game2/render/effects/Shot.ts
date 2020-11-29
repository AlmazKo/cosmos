import { LoopAnimator } from '../../../anim/Animator';

import { FireballSpell } from '../../../game/actions/FireballSpell';
import { Effect } from '../../../game/Effect';
import { TilePainter } from '../../../game/TilePainter';
import { Dir, TileType } from '../../constants';
import { Images } from '../Images';
import { World } from '../../world/World';
import { Camera } from '../Camera';
import { CELL } from '../constants';

// export const RES: Images = get('images');

export class Shot implements Effect {
  private readonly posX: index;
  private readonly posY: index;
  private readonly direction: Dir;

  private lastAnimIndex = 0;
  private shift: px = 0;
  private readonly anim: LoopAnimator;
  isFinished = false;
  private f: float = 0;
  private world: World;
  id: uint;

  constructor(private readonly images: Images, spec: FireballSpell, world: World) {
    this.id = spec.id;
    this.direction = spec.direction;
    this.posX = spec.initX;
    this.posY = spec.initY;
    this.world = world;
    this.anim = new LoopAnimator(10, (f, i) => {

        if (spec.finished) {
          this.isFinished = true;
          return true;
        }

        if (i > this.lastAnimIndex) {
          const from = this.getPosition(this.lastAnimIndex);
          const tile = this.world.nextTile(from[0], from[1], spec.direction);
          if (tile == TileType.WALL) {
            this.isFinished = true;
            return true;
          } else {
            this.lastAnimIndex = i;
          }
        }

        if (i >= 10) {
          this.isFinished = true;
          return 0;
        } else {
          this.f = f;
          this.shift = (i + f) * CELL;
          return 100;
        }
      }
    );
  }

  getPosition(animIdx: index): [px, px] {
    switch (this.direction) {

      case Dir.NORTH:
        return [this.posX, this.posY - animIdx];
      case Dir.SOUTH:
        return [this.posX, this.posY + animIdx];
      case Dir.WEST:
        return [this.posX - animIdx, this.posY];
      case Dir.EAST:
        return [this.posX + animIdx, this.posY];
    }
  }

  draw(time: DOMHighResTimeStamp, bp: TilePainter) {
  }

  draw2(time: DOMHighResTimeStamp, bp: TilePainter, camera: Camera) {

    this.anim.run(time);
    let shiftX: px = 0, shiftY: px = 0;
    let sy: px;
    switch (this.direction) {
      case Dir.NORTH:
        shiftY = -this.shift;
        break;
      case Dir.SOUTH:
        shiftY = this.shift;
        break;
      case Dir.WEST:
        shiftX = -this.shift;
        break;
      case Dir.EAST:
        shiftX = this.shift;
        break;

      default:
        return;
    }


    const x = camera.toX(this.posX) + shiftX;
    const y = camera.toY(this.posY) + shiftY;



    // bp.drawTo("fireball_32", sx, sy, 32, 32, x, y, shiftX, shiftY);
      bp.drawTo("objects", 128+48, 32+16, 16, 16, x+16, y+12, CELL/2, CELL/2);
  }

  stop(): void {
    this.anim.finish();
    this.isFinished = true;
  }

}
