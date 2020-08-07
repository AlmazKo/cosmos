import { Package } from '../../game/actions/Package';
import { Action } from './actions/Action';

export interface Api {

  listen(handler: (pkg: Package) => void): void;

  sendAction(name: string, action: Action): void;
}
