import { Package } from '../../game/actions/Package';
import { Action } from './actions/Action';
import { Api } from './Api';

export type ConnStatus = 'connecting' | 'connected' | 'disconnected'

export class WsServer implements Api {
  private handler: ((msg: Package) => void) | undefined;
  private ws: WebSocket;

  private prematurePackages: Package[] = [];
  public status: ConnStatus = 'connecting';

  constructor(url: string) {
    this.ws = new WebSocket(url);
    this.ws.onmessage = (event) => this.onRawData(JSON.parse(event.data));
    this.ws.onopen = (event) => {
      this.status = 'connected';
    };
    this.ws.onclose = (event) => {
      this.status = 'disconnected';
    };
  }

  private onRawData(data: Package) {

    if (this.handler) {
      this.handler(data)
    } else {
      this.prematurePackages.push(data)
    }
  }

  listen(handler: (msg: Package) => void) {
    this.handler = handler;

    //todo: may be make a small delay?
    this.prematurePackages.forEach(m => this.handler!!(m));
    this.prematurePackages = []
  }

  sendAction(name: string, action: Action) {

    console.log('sendAction', name, action);
    this.ws.send(JSON.stringify({op: name, ...action}))

    // if (action instanceof Step) {
    //   this.ws.send(JSON.stringify({
    //     action: "STEP", id: action.id, data: {
    //       x: action.fromPosX, y: action.fromPosY, direction: action.direction, duration: action.duration
    //     }
    //   }))
    // } else if (action instanceof FireballSpell) {
    //   this.ws.send(JSON.stringify({
    //     action: "SPELL", id: action.id, type: "FIREBALL", data: {
    //
    //       x: action.posX, y: action.posY, direction: action.direction, distance: action.distance, speed: action.duration
    //     }
    //   }))
    //
    // }

  }

}
