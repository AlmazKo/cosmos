import { Animators } from '../../anim/Animators';
import { BasePainter } from '../../draw/BasePainter';
import { CanvasContext } from '../../draw/CanvasContext';
import { Panels } from '../../game/layers/Panels';
import { style } from '../../game/styles';
import { TilePainter } from '../../game/TilePainter';
import { dirToString, stringTiles } from '../constants';
import { ActivateTrait } from '../engine/actions/ActivateTrait';
import { ProtoArrival } from '../engine/actions/ProtoArrival';
import { Game } from '../engine/Game';
import { Orientation } from '../engine/Orientation';
import { Player } from '../engine/Player';
import { Images } from '../Images';
import { Camera } from './Camera';
import { CELL, HCELL, QCELL } from './constants';
import { DrawableCreature } from './DrawableCreature';
import { LandsLayer, TILE_SIZE, TILESET_SIZE } from './LandsLayer';


const FOV_RADIUS = 8;

export class Render {

  private width: px = 100;
  private height: px = 100;
  private p: CanvasContext | undefined;
  private animators = new Animators();

  private readonly camera: Camera;
  private player: DrawableCreature | undefined;
  private phantoms = new Map<uint, DrawableCreature>();
  // @ts-ignore
  private tp: TilePainter;
  private imageData: Uint8ClampedArray | undefined;
  private readonly panels: Panels;


  constructor(
    private readonly game: Game,
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
  }


  onFrame(time: DOMHighResTimeStamp) {
    this.game.onFrame(time);

    const player = this.game.getProto();
    const camera = this.camera;
    if (!player) return;


    const actions = this.game.getActions();
    // if (actions.length > 0) return;
    //start actions
    //?

    for (const action of actions) {
      console.log("Processing action", action)

      if (action instanceof ProtoArrival) {
        camera.setTarget(action.creature.orientation);
        this.player = new DrawableCreature(action.creature)
      }

      if (action instanceof ActivateTrait) {
        this.panels.activate(action);
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

    this.animators.run(time);

    if (this.p) this.p.clear();

    camera.absoluteX = this.width / 2;
    camera.absoluteY = this.height / 2;


    this.lands.draw(time, camera);


    const p = this.player!!;


    const crp = p.creature as Player;

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

    crp.zoneCreatures.forEach((cr) => {

      // fixme deleted creatures
      //fixme optimize
      let dc = new DrawableCreature(cr);
      dc.draw2(time, this.tp, camera);
    });

    this.drawLifeLine();
    p.draw2(time, this.tp, camera);


    const o = p.orientation;
    const x = camera.toX(o.x);
    const y = camera.toY(o.y);

    this.drawFog(this.tp, camera);
    //todo debug this.p!!.rect(x, y, CELL, CELL, {style: 'red'});

    this.panels.draw(time, this.tp.p)
    // this.p!!.text(`${camera.x};${camera.y + CELL}`, x + 2, CELL + y + 2, {style: 'black'});
    this.debug(o);

    // this.p!!.text(`${p.orientation.x};${p.orientation.y}`, 3, 13, {style: 'white'});

    //draw lands
    //draw creatures
    //draw effects
    //draw fog
    // draw panels
  }


  private debug(o: Orientation) {
    const debugStyle = {style: 'white', font: '9px monospace'};
    this.p!!.text(`coord: ${o.x};${o.y}`, 3, 3, debugStyle);
    this.p!!.text(`shift: ${o.shift.toFixed(3)}`, 3, 13, debugStyle);
    this.p!!.text(`sight: ${dirToString(o.sight)}`, 3, 23, debugStyle);
    this.p!!.text(` move: ${dirToString(o.move)}`, 3, 33, debugStyle);
    this.p!!.text(`  vel: ${o.speed}`, 3, 43, debugStyle);
    this.p!!.text(' tile: ' + stringTiles[this.game.world.tileType(o.x, o.y)], 3, 53, debugStyle);
  }

  private drawFog(tp: TilePainter, camera: Camera) {

    const p = new BasePainter(tp.ctx);

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

    p.fillRect(0, 0, xL, p.height, style.fog); //LEFT
    p.fillRect(xL, 0, radius + radius + CELL, yU, style.fog);// TOP
    p.fillRect(xR, 0, p.width - xR, p.height, style.fog);//RIGHT
    p.fillRect(xL, yD, radius + radius + CELL, p.height - yD, style.fog);//BOTTOM

  }

  drawLifeLine() {
    const s = 1.0;
    const st = (s <= 0.3) ? style.dangerLifeLine : (s <= 0.75 ? style.warningLifeLine : style.goodLifeLine);

    const x = this.camera.absoluteX;
    const y = this.camera.absoluteY;

    this.p!!.ellipse(x + HCELL, y + HCELL + QCELL, HCELL, HCELL - QCELL, 0, 0.5 * Math.PI, 0.5 * Math.PI + 2 * Math.PI * s, false, st);
    // p.text(c.metrics.life + "", HCELL, CELL + 2, style.lifeText);
  }


}
