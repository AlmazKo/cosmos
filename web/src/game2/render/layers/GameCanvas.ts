// import { CanvasComposer, Pressable, Registrar } from '../../canvas/CanvasComposer';
// import { BasePainter } from '../../draw/BasePainter';
// import { ajax } from '../../util/net';
// import { Tiles } from '../api/Tiles';
// import { MapPiece } from '../api/MapPiece';
// import { Game } from '../Game';
// import { Lands } from '../Lands';
// import { KeyboardMoving } from '../../game2/controller/MovingKeys';
// import { Resources } from '../Resources';
// import { Buttons } from '../Slot';
//
// export const IMG = new Resources();
// const move     = new KeyboardMoving();
//
// export class GameCanvas implements CanvasComposer, Pressable {
//
//   // @ts-ignore
//   height: px;
//   // @ts-ignore
//   width: px;
//
//   // @ts-ignore
//   private p: BasePainter;
//   // @ts-ignore
//   private loading = 1;
//   // @ts-ignore
//   private game: Game;
//
//
//   constructor() {
//     Promise.zip(IMG.loadBasic(), ajax('/map'), ajax('/tiles'), (_, map, tiles) => {
//
//         const lands  = new Lands(map as MapPiece, tiles as Tiles);
//         this.game    = new Game(lands, move);
//         this.loading = 0;
//       }
//     ).doOnError(() => this.loading = -1);
//   }
//
//   changeSize(width: px, height: px): void {
//     this.width  = width;
//     this.height = height;
//   }
//
//   destroy(): void {
//   }
//
//   init(ctx: CanvasRenderingContext2D, width: px, height: px): void {
//     this.width  = width;
//     this.height = height;
//     this.p      = new BasePainter(ctx);
//   }
//
//   onFrame(time: DOMHighResTimeStamp, frameId?: uint): void {
//     this.p.clearArea(this.width, this.height);
//
//     const p = this.p;
//
//     if (this.loading === 1) {
//       p.text("Loading...", 30, 30)
//     } else if (this.loading === -1) {
//       p.text("ERROR", 30, 30)
//     } else {
//       this.game.onFrame(time, p)
//     }
//   }
//
//   onEndFrame(time: DOMHighResTimeStamp, error?: Error): void {
//   }
//
//   register(register: Registrar): void {
//     register('pressable', this);
//   }
//
//   keyUp(e: KeyboardEvent): void {
//     const btn = Buttons[e.keyCode];
//     if (btn) this.game.keyUp(btn)
//   }
//
//   keydown(e: KeyboardEvent): void {
//     const btn = Buttons[e.keyCode];
//     if (btn) this.game.keyDown(btn)
//   }
//
//   keypress(e: KeyboardEvent): void {
//
//   }
//
// }
