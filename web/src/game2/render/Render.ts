import {Animators} from '../../anim/Animators';
import {CanvasContext} from '../../draw/CanvasContext';
import {TilePainter} from '../../game/TilePainter';
import {ProtoArrival} from '../engine/actions/ProtoArrival';
import {StartMoving} from '../engine/actions/StartMoving';
import {Game} from '../engine/Game';
import {Images} from '../Images';
import {DrawableCreature} from './BaseCreature';
import {Camera} from './Camera';
import {CELL} from './constants';
import {LandsLayer} from './LandsLayer';


export class Render {

    private width: px = 100;
    private height: px = 100;
    private p: CanvasContext | undefined;
    private animators = new Animators();

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
        this.width = width;
        this.height = height;
        this.p = new CanvasContext(ctx);
        this.tp = new TilePainter(this.p, this.images);
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
        const ctx = this.p!!.ctx;

        // ctx.filter = 'grayscale(100%)';

        // // if (this.imageData === undefined) {
        const imageData = ctx.getImageData(0, 0, this.p!!.width, this.p!!.height);
        // // }
        var grayscale = function (data: Uint8ClampedArray) {
          for (var i = 0; i < data.length; i += 4) {
            var avg     = (data[i] + data[i + 1] + data[i + 2]) / 3;
            data[i]     = avg; // red
            data[i + 1] = avg; // green
            data[i + 2] = avg; // blue
          }
        };
        // //
        // // if ((window as any).wasm !== undefined && this.imageData !== undefined) {
        // //     // const arr = (window as any).wasm.grayscale(this.imageData);
        // //     const idata = new ImageData(this.imageData, this.p!!.width, this.p!!.height);
        //     grayscale(imageData.data);
        //     ctx.putImageData(imageData, 0, 0);
        // // }


        const p = this.player!!;
        p.draw2(time, this.tp, camera);


        const x = camera.toX(p.orientation.x);
        const y = camera.toY(p.orientation.y);

        this.p!!.rect(x, y, CELL, CELL, {style: 'red'});
        // this.p!!.text(`${camera.x};${camera.y + CELL}`, x + 2, CELL + y + 2, {style: 'black'});
        this.p!!.text(`${p.orientation.shift.toFixed(3)}`, 10, 100, {style: 'black'});

        //draw lands
        //draw creatures


        //draw effects
        //draw fog


        // draw panels


    }

}
