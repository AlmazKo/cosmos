import { Ballad } from './canvas/Ballad';
import './ext/array/ext';
import './ext/promiser/ext';
import { GamePad, gamepadSchema } from './game2/controller/GamePad';
import { Keyboard, keyboardSchema } from './game2/controller/Keyboard';
import { GameCanvas } from './game2/render/GameCanvas';
import { MiniMapCanvas } from './game2/render/MiniMapCanvas';
import { Render } from './game2/render/Render';
import { get } from './Module';

export const HOST = "https://localhost";
export const WS_HOST = "wss://localhost/ws";

window.onload = () => {
  let div = document.getElementById("game")!!;
  let div2 = document.getElementById("minimap")!!;
  console.info(div);
  let p = new Ballad(div);
  const gc = get(GameCanvas);
  get(Keyboard);
  p.start(gc);


  let p2 = new Ballad(div2);
  p2.start(new MiniMapCanvas(get(Render)));

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

  var myWorker = new Worker('worker.ts');

  myWorker.onmessage = function (e) {
    console.log('Message received from worker', e);
  }
};




