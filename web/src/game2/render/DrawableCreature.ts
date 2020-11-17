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

const mapSmall: px[] = [];

mapSmall[Dir.NORTH] = 64;
mapSmall[Dir.SOUTH] = 0;
mapSmall[Dir.EAST] = 32;
mapSmall[Dir.WEST] = 96;

const mapNpc: px[] = [];

mapNpc[Dir.NORTH] = 64;
mapNpc[Dir.SOUTH] = 0;
mapNpc[Dir.EAST] = 32;
mapNpc[Dir.WEST] = 96;

enum State {
  RUN,
  STAND,
  MELEE,
  CAST,
  DEAD
}

export class DrawableCreature implements TileDrawable {

  readonly orientation: Orientation;
  public readonly creature: Creature;

  private animators = new Animators();
  private showInstantSpell = false;
  private showDamaged = false;
  private meleeFactor: float = 0;
  private damaged = false;
  private state: State = State.RUN;

  constructor(c: Creature) {
    this.creature = c;
    this.orientation = c.orientation;
  }

  draw(time: DOMHighResTimeStamp, bp: TilePainter) {

  }

  draw2(time: DOMHighResTimeStamp, p: CanvasContext, bp: TilePainter, camera: Camera) {

    let shift = (time % 400) / 400;
    this.drawLifeLine(p, camera);
    const o = this.orientation;
    if (this.state == State.RUN && o.offset === 0) {
      shift = 0;
    }

    this.animators.run(time);

    let x: px, y: px;

    let sy = mapSmall[o.sight];
    let sx = Math.floor(Math.abs(shift) * 4) * 32;
    if (this.state === State.MELEE) {
      sy += 128;
    } else if (this.state === State.CAST) {
      sx += 128;
    }

    if (this.creature instanceof Player) {
      x = camera.absoluteX;
      y = camera.absoluteY;
      let sw = 32, sh = 32;
      bp.drawTo("character", sx, sy, sw, sh, x, y, CELL, CELL);
    } else if (this.creature.id < 10000) {
      x = camera.toX2(this.creature.orientation);
      y = camera.toY2(this.creature.orientation);

      let sw = 32, sh = 32;
      bp.drawTo("character_2", sx, sy, sw, sh, x, y, CELL, CELL);
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

      x = camera.toX2(this.creature.orientation);
      y = camera.toY2(this.creature.orientation);

      //64-16=48/2=24
      //64-32=32/2=16
      bp.drawTo(asset, sx, sy, sw, sh, x + 16, y + 8, sw, sh);
    }


   this.drawName(bp, x, y);
  }


  private drawName(bp: TilePainter, x: number, y: number) {
    const c = this.creature;
    // bp.p.text(c.metrics.name + "", x, y, style.creatureNameBg);
    bp.p.text(c.metrics.name, x + HCELL, y, style.creatureNameBg)
    bp.p.text(c.metrics.name, x + HCELL - 1, y - 1, style.creatureName)
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

  melee() {
    this.state = State.MELEE;
    this.animators.set("instant_spell", new Delay(400), () => this.state = State.RUN);
  }

  instantSpell() {
    this.state = State.CAST;
    this.animators.set("instant_spell", new Delay(200), () => this.state = State.RUN);
  }

  damage() {
    if (this.damaged) return

    this.damaged = true;
    this.animators.set("damaged", new Delay(200), () => this.damaged = false);
    this.animators.set("show_amount_damage", new Delay(200), () => this.damaged = false);

  }
}
