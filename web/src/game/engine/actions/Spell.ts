import {FireballSpell} from '../../api/FireballSpell';
import {ShotSpell} from '../../api/ShotSpell';
import {Act} from '../Act';
import {Actor} from '../Actor';

export class Spell implements Act {

  constructor(readonly id: uint,
              readonly actor: Actor,
              readonly startTime: tsm,
              readonly spell: FireballSpell|ShotSpell|any) {

  }

}
