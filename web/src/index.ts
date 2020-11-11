import { Ballad } from './canvas/Ballad';
import './ext/array/ext';
import './ext/promiser/ext';
import { GamePad } from './game2/controller/GamePad';
import { Keyboard } from './game2/controller/Keyboard';
import { Game } from './game2/engine/Game';
import { GameCanvas } from './game2/render/GameCanvas';
import { MiniMapCanvas } from './game2/render/MiniMapCanvas';
import { Render } from './game2/render/Render';
import { World } from './game2/world/World';
import { get } from './Module';

export const HOST = "https://localhost";
export const WS_HOST = "wss://localhost";

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

  window.addEventListener('gamepadconnected', e => get(GamePad));

  var myWorker = new Worker('worker.ts');

  myWorker.onmessage = function (e) {
    console.log('Message received from worker', e);
  }
};




