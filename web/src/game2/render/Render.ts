import { Animators } from '../../anim/Animators';
import { BasePainter } from '../../draw/BasePainter';
import { CanvasContext } from '../../draw/CanvasContext';
import { style } from '../../game/styles';
import { TilePainter } from '../../game/TilePainter';
import { ProtoArrival } from '../engine/actions/ProtoArrival';
import { StartMoving } from '../engine/actions/StartMoving';
import { Game } from '../engine/Game';
import { Images } from '../Images';
import { DrawableCreature } from './BaseCreature';
import { Camera } from './Camera';
import { CELL, HCELL, QCELL } from './constants';
import { LandsLayer } from './LandsLayer';


export class Render {

  private width: px  = 100;
  private height: px = 100;
  private p: CanvasContext | undefined;
  private animators  = new Animators();

  private readonly camera: Camera;
  private player: DrawableCreature | undefined;
  private phantoms: DrawableCreature[] = [];
  // @ts-ignore
  private tp: TilePainter;
  private imageData: Uint8ClampedArray | undefined;


  constructor(
    private readonly game: Game,
    private readonly lands: LandsLayer,
    private readonly images: Images
  ) {
    this.camera = new Camera(0, -4, 1);
  }


  updateContext(ctx: CanvasRenderingContext2D, width: px, height: px): void {
    this.width  = width;
    this.height = height;
    this.p      = new CanvasContext(ctx);
    this.tp     = new TilePainter(this.p, this.images);
    this.lands.init(this.p);
    this.lands.changeSize(width, height);
  }


  onFrame(time: DOMHighResTimeStamp) {

    const player = this.game.getProto();
    const camera = this.camera;
    if (!player) return;


    const actions = this.game.getActions();
    // if (actions.length > 0) return;


    //start actions

    //?

    for (const action of actions) {

      if (action instanceof ProtoArrival) {
        camera.setTarget(action.creature.orientation);
        this.player = new DrawableCreature(action.creature)
      }

      if (action instanceof StartMoving) {
        //fixme
        // this.player!!.startMoving(action)
      }

    }

    this.animators.run(time);

    if (this.p) this.p.clear();

    camera.absoluteX = this.width / 2;
    camera.absoluteY = this.height / 2;


    this.lands.draw(time, camera);


    const p = this.player!!;
    this.drawLifeLine();
    p.draw2(time, this.tp, camera);


    const x = camera.toX(p.orientation.x);
    const y = camera.toY(p.orientation.y);

    this.drawFog(this.tp, camera);
    this.p!!.rect(x, y, CELL, CELL, {style: 'red'});

    // this.p!!.text(`${camera.x};${camera.y + CELL}`, x + 2, CELL + y + 2, {style: 'black'});


    this.p!!.text(`${p.orientation.shift.toFixed(3)}`, 3, 3, {style: 'white'});
    this.p!!.text(`${p.orientation.x};${p.orientation.y}`, 3, 13, {style: 'white'});
    //draw lands
    //draw creatures
    //draw effects
    //draw fog
    // draw panels


  }


  private drawFog(tp: TilePainter, camera: Camera) {

    const p = new BasePainter(tp.ctx);

    // if (this.proto.isDead) {
    //   p.fillRect(0, 0, p.width, p.height, style.fog);
    //   return;
    // }

    const radius = (9) * CELL;
    const x      = camera.absoluteX;
    const y      = camera.absoluteY;
    const xL     = x - radius;
    const xR     = x + radius + CELL;
    const yU     = y - radius;
    const yD     = y + radius + CELL;

    p.fillRect(0, 0, xL, p.height, style.fog); //LEFT
    p.fillRect(xL, 0, radius + radius + CELL, yU, style.fog);// TOP
    p.fillRect(xR, 0, p.width - xR, p.height, style.fog);//RIGHT
    p.fillRect(xL, yD, radius + radius + CELL, p.height - yD, style.fog);//BOTTOM

  }

  drawLifeLine() {
    const s  = 1.0;
    const st = (s <= 0.3) ? style.dangerLifeLine : (s <= 0.75 ? style.warningLifeLine : style.goodLifeLine);

    const x = this.camera.absoluteX;
    const y = this.camera.absoluteY;

    this.p!!.ellipse(x + HCELL, y + HCELL + QCELL, HCELL, HCELL-QCELL, 0, 0.5 * Math.PI, 0.5 * Math.PI + 2 * Math.PI * s, false, st);
    // p.text(c.metrics.life + "", HCELL, CELL + 2, style.lifeText);
  }


}
