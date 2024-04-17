import {Audios} from './game/audio/Audios';
import {GamePad} from './game/controller/GamePad';
import {Keyboard} from './game/controller/Keyboard';
import {Game} from './game/engine/Game';
import {Moving} from './game/engine/Moving';
import {Spells} from './game/engine/Spells';
import {GameCanvas} from './game/render/GameCanvas';
import {LandsLayer} from './game/render/LandsLayer';
import {Render} from './game/render/Render';
import {AudioAssets} from './game/server/AudioAssets';
import {ImageAssets} from './game/server/ImageAssets';
import {ResourcesServer} from './game/server/ResourcesServer';
import {WsServer} from './game/server/WsServer';
import {World} from './game/world/World';
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

