import { Ballad } from './canvas/Ballad';
import './ext/array/ext';
import './ext/promiser/ext';
import { GamePad } from './game2/controller/GamePad';
import { Keyboard } from './game2/controller/Keyboard';
import { GameCanvas } from './game2/render/GameCanvas';
import { get } from './Module';

export const HOST    = "https://localhost";
export const WS_HOST = "wss://localhost";

window.onload = () => {
  let div = document.getElementById("game")!!;
  console.info(div);
  let p     = new Ballad(div);
  const gc  = get(GameCanvas);
  const kbd = get(Keyboard);
  p.start(gc);

  window.addEventListener('gamepadconnected', e => get(GamePad));

  var myWorker = new Worker('worker.ts');

  myWorker.onmessage = function (e) {
    console.log('Message received from worker', e);
  }
};




