import { Audios } from './game2/audio/Audios';
import { GamePad } from './game2/controller/GamePad';
import { Keyboard } from './game2/controller/Keyboard';
import { Game } from './game2/engine/Game';
import { Moving } from './game2/engine/Moving';
import { Spells } from './game2/engine/Spells';
import { GameCanvas } from './game2/render/GameCanvas';
import { LandsLayer } from './game2/render/LandsLayer';
import { Render } from './game2/render/Render';
import { AudioAssets } from './game2/server/AudioAssets';
import { ImageAssets } from './game2/server/ImageAssets';
import { ResourcesServer } from './game2/server/ResourcesServer';
import { WsServer } from './game2/server/WsServer';
import { World } from './game2/world/World';
import {AdminCanvas} from "./admin/AdminCanvas";


export const HOST = "https://localhost";
export const WS_HOST = "wss://localhost/ws";
export const WS_HOST_ADMIN = "wss://localhost/ws/admin";

interface Constructor<T = any> {
  new(..._: any[]): T;
}

const data: Map<Constructor | string, any> = new Map();
const cached: Map<Constructor | string, any> = new Map();


function set<T>(c: Constructor<T> | string, factory: () => T) {
  data.set(c, factory)
}

function setCached<T>(c: Constructor<T> | string, factory: () => T) {
  data.set(c, () => {

    const result = cached.get(c);

    if (result === undefined) {
      cached.set(c, factory())
    }

    return cached.get(c)
  })
}

export function get<T>(c: Constructor<T> | string): T {
  const f = data.get(c);
  if (f === undefined) console.warn('DI2', "Not found factory: " + c);

  return f()
}

setCached('api', () => new WsServer(WS_HOST));
setCached('admin-api', () => new WsServer(WS_HOST_ADMIN));
setCached('map', () => new ResourcesServer(HOST));
setCached('images', () => new ImageAssets(HOST));
// setCached(LocalServer, () => new LocalServer());
setCached(Moving, () => new Moving());
setCached(AudioContext, () => new AudioContext());
setCached(AudioAssets, () => new AudioAssets(HOST, get(AudioContext)));
setCached(Audios, () => new Audios(get(AudioContext), get(AudioAssets)));
setCached(Keyboard, () => new Keyboard(get(Moving), get(Game)));
setCached(GamePad, () => new GamePad(get(Moving), get(Game)));
setCached(Spells, () => new Spells());
setCached(World, () => new World(get('map')));
setCached(Game, () => new Game(get('api'), get(World), get(Moving), get(Spells), get(Audios)));
setCached(LandsLayer, () => new LandsLayer(get(World), get('images')));
setCached(Render, () => new Render(get(Game), get(LandsLayer), get(Spells), get('images')));
setCached(GameCanvas, () => new GameCanvas(get(Render)));
setCached(AdminCanvas, () => new AdminCanvas(get('admin-api')));

