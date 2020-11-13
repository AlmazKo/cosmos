import { Delay } from '../../anim/Animator';
import { Animators } from '../../anim/Animators';
import { CanvasContext } from '../../draw/CanvasContext';
import { style } from '../../game/styles';
import { TileDrawable } from '../../game/TileDrawable';
import { TilePainter } from '../../game/TilePainter';
import { Dir } from '../constants';
import { Creature } from '../engine/Creature';
import { Orientation } from '../engine/Orientation';
import { Player } from '../engine/Player';
import { Camera } from './Camera';
import { CELL, HCELL } from './constants';


const map: px[] = [];

map[Dir.NORTH] = 0;
map[Dir.SOUTH] = 128;
map[Dir.EAST] = 196;
map[Dir.WEST] = 64;

const mapNpc: px[] = [];

mapNpc[Dir.NORTH] = 64;
mapNpc[Dir.SOUTH] = 0;
mapNpc[Dir.EAST] = 32;
mapNpc[Dir.WEST] = 96;


export class DrawableCreature implements TileDrawable {

  readonly orientation: Orientation;
  public readonly creature: Creature;

  private animators = new Animators();
  private showInstantSpell = false;
  private showDamaged = false;
  private meleeFactor: float = 0;
  private damaged = false;

  constructor(c: Creature) {
    this.creature = c;
    this.orientation = c.orientation;
  }

  draw(time: DOMHighResTimeStamp, bp: TilePainter) {

  }

  draw2(time: DOMHighResTimeStamp, p: CanvasContext, bp: TilePainter, camera: Camera) {

    this.drawLifeLine(p, camera);
    const o = this.orientation;

    this.animators.run(time);

    let x: px, y: px;
    if (this.creature instanceof Player) {
      x = camera.absoluteX;
      y = camera.absoluteY;
      let sy = map[o.sight] + 2;
      let sx = Math.floor(Math.abs(o.shift) * 9) * 64;
      // drawLifeLine(bp.toInDirect(x, y), this);

      let sw = 64, sh = 64;
      bp.drawTo("ch", sx, sy, sw, sh, x, y, CELL, CELL);
    } else {
      x = camera.toX2(this.creature.orientation);
      y = camera.toY2(this.creature.orientation);

      if (this.creature.id < 10000) {
        let sy = map[o.sight];
        let sx = Math.floor(Math.abs(o.shift) * 9) * 64;

        let sw = 64, sh = 64;
        bp.drawTo("ch_alien", sx, sy, sw, sh, x, y, CELL, CELL);
      } else {
        let sy = mapNpc[o.sight];
        let sx = Math.floor(Math.abs(o.shift) * 4) * 16;

        let sw = 16, sh = 32;

        let asset;
        if (this.damaged) {
          asset = "NPC_test_dmg";
        } else {
          asset = "NPC_test";
        }

        //64-16=48/2=24
        //64-32=32/2=16
        bp.drawTo(asset, sx, sy, sw, sh, x + 16, y + 8, sw, sh);
      }
    }

    this.drawName(bp, x, y);
  }


  private drawName(bp: TilePainter, x: number, y: number) {
    const c = this.creature;
    // bp.p.text(c.metrics.name + "", x, y, style.creatureNameBg);
    bp.p.text(c.metrics.name, x + HCELL + 0.5, y - 1.5, style.creatureNameBg)
    bp.p.text(c.metrics.name, x + HCELL, y - 2, style.creatureName)
  }

  drawLifeLine(bp: CanvasContext, camera: Camera) {
    if (this.creature.metrics.life >= 100) {
      return;
    }
    const s = this.creature.metrics.life / 100;
    const st = (s <= 0.3) ? style.dangerLifeLine : (s <= 0.75 ? style.warningLifeLine : style.goodLifeLine);

    const x = camera.toX2(this.creature.orientation);
    const y = camera.toY2(this.creature.orientation);


    bp.fillRect(x + 4, y, CELL - 8, 3, '#00000066')
    bp.fillRect(x + 4, y, (CELL - 8) * s, 3, st.style)
  }


  // onRotated(rotated: boolean) {
  //   this.rotated = rotated;
  // }
  //
  //
  // onStep(step: Step): void {
  //
  //   //todo add server sync
  // }
  //
  // melee() {
  //   this.animators.set("melee",
  //     new Animator(250, f => this.meleeFactor = f),
  //     () => this.meleeFactor = 0);
  // }
  //
  instantSpell() {
    this.showInstantSpell = true;
    this.animators.set("instant_spell", new Delay(100), () => this.showInstantSpell = false);
  }

  damage() {
    if (this.damaged) return

    this.damaged = true;
    this.animators.set("damaged", new Delay(200), () => this.damaged = false);
    this.animators.set("show_amount_damage", new Delay(200), () => this.damaged = false);

  }
}
