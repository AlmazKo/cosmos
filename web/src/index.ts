import { Player } from './canvas/Player';
import './ext/array/ext';
import './ext/promiser/ext';
import { GamePad, gamepadSchema } from './game2/controller/GamePad';
import { Keyboard, keyboardSchema } from './game2/controller/Keyboard';
import { GameCanvas } from './game2/render/GameCanvas';
import { MiniMapCanvas } from './game2/render/layers/MiniMapCanvas';
import { TileInfoCanvas } from './game2/render/layers/TileInfoCanvas';
import { Render } from './game2/render/Render';
import { get } from './Module';



window.onload = () => {
  let div = document.getElementById("game")!!;
  let div2 = document.getElementById("minimap")!!;
  let div3 = document.getElementById("tileinfo")!!;
  let p = new Player(div);
  const gc = get(GameCanvas);
  get(Keyboard);
  p.start(gc);


  let p2 = new Player(div2, undefined, 24);
  p2.start(new MiniMapCanvas(get(Render)));

  let p3 = new Player(div3, undefined, 30);
  p3.start(new TileInfoCanvas(get(Render), get('images')));

  gc.render.panels.onHotKeysUpdate(keyboardSchema);
  window.addEventListener('gamepadconnected', e => {
    console.log('Gamepad connected');
    get(GamePad);
    gc.render.panels.onHotKeysUpdate(gamepadSchema);
  });
  window.addEventListener('ongamepaddisconnected', e => {
    //fixme it doesnt work
    console.log('Gamepad disconnected');
    gc.render.panels.onHotKeysUpdate(keyboardSchema);
  });
  //
  // const myWorker = new Worker('worker.ts');
  //
  // myWorker.onmessage = function (e) {
  //   console.log('Message received from worker', e);
  // }
};




