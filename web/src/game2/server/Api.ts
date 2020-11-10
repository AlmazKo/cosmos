import { Package } from '../../game/actions/Package';
import { Action } from './actions/Action';
import { ConnStatus } from './WsServer';

export interface Api {
  status: ConnStatus

  listen(handler: (pkg: Package) => void): void;

  sendAction(name: string, action: Action): void;
}
