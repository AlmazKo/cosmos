import { Animators } from '../../anim/Animators';
import { BasePainter } from '../../draw/BasePainter';
import { CanvasContext } from '../../draw/CanvasContext';
import { Effects } from '../../game/Effects';
import { style } from '../../game/styles';
import { TilePainter } from '../../game/TilePainter';
import { Dir, dirToString, stringTiles } from '../constants';
import { Act } from '../engine/Act';
import { ActivateTrait } from '../engine/actions/ActivateTrait';
import { OnDamage } from '../engine/actions/OnDamage';
import { OnMeleeAttack } from '../engine/actions/OnMeleeAttack';
import { ProtoArrival } from '../engine/actions/ProtoArrival';
import { Spell } from '../engine/actions/Spell';
import { Creature } from '../engine/Creature';
import { Game } from '../engine/Game';
import { Player } from '../engine/Player';
import { Images } from '../Images';
import { TraitFireball, TraitMelee } from '../Trait';
import { Camera } from './Camera';
import { CELL, HCELL, QCELL } from './constants';
import { DrawableCreature } from './DrawableCreature';
import { DamageEffect } from './effects/DamageEffect';
import { Fireball } from './effects/Fireball';
import { LandsLayer, TILE_SIZE, TILESET_SIZE } from './LandsLayer';
import { Panels } from './layers/Panels';


const FOV_RADIUS = 8;
const DEBUG = true;

export class Render {

  private width: px = 100;
  private height: px = 100;
  private p: CanvasContext | undefined;
  private animators = new Animators();

  readonly camera: Camera;
  private player: DrawableCreature | undefined;
  private phantoms = new Map<uint, DrawableCreature>();
  // @ts-ignore
  private tp: TilePainter;
  private imageData: Uint8ClampedArray | undefined;
  readonly panels: Panels;
  private effects = new Effects();
  private cursor: [px, px] | undefined;

  constructor(
    readonly game: Game,
    private readonly lands: LandsLayer,
    private readonly images: Images
  ) {
    this.camera = new Camera();
    this.panels = new Panels(images);
  }

  updateContext(ctx: CanvasRenderingContext2D, width: px, height: px): void {
    this.width = width;
    this.height = height;
    this.p = new CanvasContext(ctx);
    this.tp = new TilePainter(this.p, this.images);
    this.lands.init(this.p);
    this.lands.changeSize(width, height);

    ctx.imageSmoothingEnabled = false;
  }


  onFrame(time: DOMHighResTimeStamp) {
    this.game.onFrame(time);

    const player = this.game.getProto();
    const camera = this.camera;
    if (!player) return;
    const actions = this.game.getActions();
    this.processActions(actions, camera);

    this.animators.run(time);

    if (this.p) this.p.clear();

    camera.absoluteX = this.width / 2;
    camera.absoluteY = this.height / 2;
    this.lands.draw(time, camera);
    const p = this.player!!;
    const crp = p.creature as Player;
    if (DEBUG) this.drawRealPosition();

    crp.zoneObjects.forEach((obj) => {

      const x = camera.toX(obj.x);
      const y = camera.toY(obj.y);

      const tileX = obj.tileId % TILESET_SIZE;
      const tileY = Math.floor(obj.tileId / TILESET_SIZE);
      const sx = TILE_SIZE * tileX;
      const sy = TILE_SIZE * tileY;

      let sw = 32, sh = 32;
      this.tp.drawTo("map1", sx, sy, sw, sh, x, y, CELL, CELL);
      //debug this.p!!.rect(x, y, CELL, CELL, {style: '#fff'})
    })

    this.phantoms.forEach(dc => {
      if (!crp.zoneCreatures.has(dc.creature.id)) {
        this.phantoms.delete(dc.creature.id);
      }
    });

    crp.zoneCreatures.forEach((cr) => {
      const dc = this.getDrawable(cr);
      dc.draw2(time, this.p!!, this.tp, camera);
    });

    p.draw2(time, this.p!!, this.tp, camera);
    if (p.creature.isDead()) {
      this.drawDeath(this.tp, camera);
    } else {
      this.drawFog(this.tp, camera);
    }

    this.effects.draw2(time, this.tp, camera);
    this.drawCursorPosition();
    this.drawConnectionStatus();
    this.panels.draw(time, this.tp.p)
    if (DEBUG) this.debug();
  }

  private processActions(actions: Act[], camera: Camera) {
    for (const action of actions) {
      // console.log("Processing action", action)

      if (action instanceof ProtoArrival) {
        camera.setTarget(action.creature.orientation);
        this.player = new DrawableCreature(action.creature)
      }

      if (action instanceof ActivateTrait) {
        this.panels.activate(action);

        if (action.trait instanceof TraitMelee) {
          this.player!!.melee();
        } else if (action.trait instanceof TraitFireball) {
          this.player!!.instantSpell();
        }
      }

      if (action instanceof OnDamage) {

        const victim = (this.player!!.creature as Player).zoneCreatures.get(action.victim.id);
        if (victim) {
          const dc = this.getDrawable(victim);
          dc.damage()
        }
        this.effects.push(new DamageEffect(action));
      }

      if (action instanceof Spell) {
        this.effects.push(new Fireball(this.images, action.spell, this.game.world));
      }


      if (action instanceof OnMeleeAttack) {
        const dc = this.getDrawable(action.creature);
        dc.melee();
      }


      //
      // if (action instanceof StartMoving) {
      //   if (!this.phantoms.has(action.creature.id)) {
      //     this.phantoms.set(action.creature.id, new DrawableCreature(action.creature))
      //   }
      //   //fixme
      //   // this.player!!.startMoving(action)
      // }

    }
  }

  private getDrawable(cr: Creature): DrawableCreature {
    let dc = this.phantoms.get(cr.id);
    if (!dc) {
      dc = new DrawableCreature(cr);
      this.phantoms.set(cr.id, dc)
    }
    return dc!!;
  }

  private drawRealPosition() {
    if (!this.game.protoReal) return;


    const p = this.p!!;
    const a = this.game.getProto()!!;
    let x = this.camera.toX(a.orientation.x);
    let y = this.camera.toY(a.orientation.y);
    p.fillRect(x, y, CELL, CELL, '#ffffff66');

    const o = this.game.protoReal;
    x = this.camera.toX(o.x);
    y = this.camera.toY(o.y);

    p.rect(x, y, CELL, CELL, 'red');
    switch (o.sight) {
      case Dir.NORTH:
        p.hline(x, x + CELL, y + 3, 'red');
        break;
      case Dir.WEST:
        p.vline(x + 3, y, y + CELL, 'red');
        break;
      case Dir.EAST:
        p.vline(x + CELL - 3, y, y + CELL, 'red');
        break;
      case Dir.SOUTH:
        p.hline(x, x + CELL, y + CELL - 3, 'red');
        break;
    }

  }

  private drawCursorPosition() {
    if (!this.cursor || !this.player) return;

    const c = this.camera;
    const posCursorX = c.toPosX(this.cursor[0]);
    const posCursorY = c.toPosY(this.cursor[1]);
    const x = c.toX(posCursorX);
    const y = c.toY(posCursorY);
    const tile = this.game.world.tileType(posCursorX, posCursorY);
    const p = this.p!!;
    p.fillRect(x - 1, y + CELL, CELL + 2, 24, '#ffffff99')
    p.text(stringTiles[tile].toLowerCase(), x + HCELL, y + CELL, style.cellInfo)
    p.text(posCursorX + ',' + posCursorY, x + HCELL, y + CELL + 13, style.cellInfo)
    p.rect(x, y, CELL, CELL, {style: 'white', width: 1.5});
  }

  fov(r: uint): Array<[pos, pos]> {

    const cx = this.camera.target.x;
    const cy = this.camera.target.y;
    const zone: Array<[pos, pos]> = [];

    for (let ix = cx - r; ix <= cx + r; ix++) {
      this.race(cx, cy, ix, cy - r, zone);
    }

    for (let ix = cx - r; ix <= cx + r; ix++) {
      this.race(cx, cy, ix, cy + r, zone);
    }

    for (let iy = cy - r; iy <= cy + r; iy++) {
      this.race(cx, cy, cx - r, iy, zone);
    }

    for (let iy = cy - r; iy <= cy + r; iy++) {
      this.race(cx, cy, cx + r, iy, zone);
    }
    return zone;
  }

  // https://stackoverflow.com/questions/4672279/bresenham-algorithm-in-javascript
  private line(x0: pos, y0: pos, x1: pos, y1: pos) {
    let dx = Math.abs(x1 - x0);
    let dy = Math.abs(y1 - y0);
    let sx = (x0 < x1) ? 1 : -1;
    let sy = (y0 < y1) ? 1 : -1;
    let err = dx - dy;

    while (true) {

      if (this.game.world.canSeeThrough(x0, y0)) {
        this.p!!.fillRect(this.camera.toX(x0), this.camera.toY(y0), CELL, CELL, '#ffffff33');
      } else {
        return
      }

      if ((x0 === x1) && (y0 === y1)) break;
      var e2 = 2 * err;
      if (e2 > -dy) {
        err -= dy;
        x0 += sx;
      }
      if (e2 < dx) {
        err += dx;
        y0 += sy;
      }
    }
  }

  private race(x0: pos, y0: pos, x1: pos, y1: pos, zone: Array<[pos, pos]>) {
    let dx = Math.abs(x1 - x0);
    let dy = Math.abs(y1 - y0);
    let sx = (x0 < x1) ? 1 : -1;
    let sy = (y0 < y1) ? 1 : -1;
    let err = dx - dy;

    while (true) {

      if (zone.findIndex(([x, y]) => x === x0 && y == y0) === -1) {
        if (this.game.world.canSeeThrough(x0, y0)) {
          zone.push([x0, y0]);
        } else {
          return
        }
      }

      if ((x0 === x1) && (y0 === y1)) break;
      var e2 = 2 * err;
      if (e2 > -dy) {
        err -= dy;
        x0 += sx;
      }
      if (e2 < dx) {
        err += dx;
        y0 += sy;
      }
    }
  }

  private debug() {
    if (!this.player) return;

    const proto = this.player!!.creature as Player;
    const o = proto.orientation;
    const debugStyle = {style: 'white', font: '9px monospace'};
    const p = this.p!!;
    let y = 3;
    p.text(`    coord: ${o.x};${o.y}`, 3, y, debugStyle);
    p.text(`    shift: ${o.shift.toFixed(3)}`, 3, y += 10, debugStyle);
    p.text(`    sight: ${dirToString(o.sight)}`, 3, y += 10, debugStyle);
    p.text(`     move: ${dirToString(o.move)}`, 3, y += 10, debugStyle);
    p.text(`      vel: ${o.speed}`, 3, y += 10, debugStyle);
    p.text('     tile: ' + stringTiles[this.game.world.tileType(o.x, o.y)], 3, y += 10, debugStyle);
    p.text('   cursor: ' + this.cursor, 3, y += 10, debugStyle);
    p.text('   camera: ' + this.camera.absoluteX, 3, y += 10, debugStyle);
    p.text('creatures: ' + proto.zoneCreatures.size, 3, y += 10, debugStyle);
    p.text('  objects: ' + proto.zoneObjects.size, 3, y += 10, debugStyle);
  }

  private drawDeath(tp: TilePainter, camera: Camera) {
    const p = new BasePainter(tp.ctx);
    p.fillRect(0, 0, p.width, p.height, style.death);
    p.text("You are dead", this.width / 2, this.height / 2, style.protoDeathTitle);
  }

  private drawFog(tp: TilePainter, camera: Camera) {

    const p = new BasePainter(tp.ctx); //todo optimize!!!!!

    // if (this.proto.isDead) {
    //   p.fillRect(0, 0, p.width, p.height, style.fog);
    //   return;
    // }

    const radius = FOV_RADIUS * CELL;
    const x = camera.absoluteX;
    const y = camera.absoluteY;
    const xL = x - radius;
    const xR = x + radius + CELL;
    const yU = y - radius;
    const yD = y + radius + CELL;
    //
    // p.fillRect(0, 0, xL, p.height, style.fog); //LEFT
    // p.fillRect(xL, 0, radius + radius + CELL, yU, style.fog);// TOP
    // p.fillRect(xR, 0, p.width - xR, p.height, style.fog);//RIGHT
    // p.fillRect(xL, yD, radius + radius + CELL, p.height - yD, style.fog);//BOTTOM


    const cx = this.camera.target.x;
    const cy = this.camera.target.y;
    const fovRadius = FOV_RADIUS + 14;
    const zone = this.fov(FOV_RADIUS);

    for (let xx = cx - fovRadius; xx < cx + fovRadius; xx++) {
      for (let yy = cy - fovRadius; yy < cy + fovRadius; yy++) {
        if (zone.findIndex(([x, y]) => x === xx && y == yy) === -1) {
          p.fillRect(this.camera.toX(xx), this.camera.toY(yy), CELL, CELL, style.lightFog);
        }
      }
    }
    //
    // zone.forEach((p) => {
    //   this.p!!.fillRect(this.camera.toX(p[0]), this.camera.toY(p[1]), CELL, CELL, '#ffffff66');
    // });


  }

  private drawConnectionStatus() {
    const p = this.p!!;
    const text = this.game.getConnectionStatus();
    const width = p.measureWidth(text, style.connectionStatus)
    const x = this.width - width - 10;
    const y = this.height - 8;

    let color;
    switch (this.game.getConnectionStatus()) {
      case 'connecting':
        color = '#ff0';
        break;
      case 'connected':
        color = '#0f0';
        break;
      case 'disconnected':
        color = '#f00';
        break;

    }
    p.fillCircle(x, y, 4, color)
    p.text(text, x + 6, y, {...style.connectionStatus, style: color})
  }

  drawLifeLine() {
    const s = 1.0;
    const st = (s <= 0.3) ? style.dangerLifeLine : (s <= 0.75 ? style.warningLifeLine : style.goodLifeLine);

    const x = this.camera.absoluteX;
    const y = this.camera.absoluteY;

    this.p!!.ellipse(x + HCELL, y + HCELL + QCELL, HCELL, HCELL - QCELL, 0, 0.5 * Math.PI, 0.5 * Math.PI + 2 * Math.PI * s, false, st);
    // p.text(c.metrics.life + "", HCELL, CELL + 2, style.lifeText);
  }

  changeCursorPosition(pos: [px, px] | undefined) {
    this.cursor = pos;
  }

  changeSize(width: px, height: px) {
    this.width = width;
    this.height = height;
  }
}
