import { ApiMessage } from '../../game/actions/ApiMessage';
import { Package } from '../../game/actions/Package';
import { Action } from './actions/Action';
import { Api } from './Api';


export class WsServer implements Api {
  private handler: ((msg: Package) => void) | undefined;
  private ws: WebSocket;

  private prematurePackages: Package[] = [];

  constructor(url: string) {
    this.ws           = new WebSocket(url);
    this.ws.onmessage = (event) => this.onRawData(JSON.parse(event.data))
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


    const pkg = {
      tick    : 0,
      time    : 0,
      messages: [
        {
          id    : 1,
          action: "PROTAGONIST_ARRIVAL",
          type  : "",
          data  : {
            "creature": {
              "id"          : 4,
              "isPlayer"    : true,
              "x"           : 18,
              "y"           : 0,
              "direction"   : 2,
              "metrics"     : {"name": "4", "life": 50, "maxLife": 50},
              "viewDistance": 10
            }

          }
        } as ApiMessage]
    } as Package;


    handler(pkg)
  }

  sendAction(action: Action) {

    console.log("Action >", action);


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
