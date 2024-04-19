import {Act} from '../Act';
import {Player} from '../Player';

export class ProtoArrival implements Act {

  constructor(readonly id: uint,
              readonly actor: Player,
              readonly startTime: tsm) {

  }

}
