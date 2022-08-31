import {TermicaCommand} from './TermicaCommand';

export class AbreGavetaElgin extends TermicaCommand {
  constructor() {
    super('AbreGavetaElgin');
  }

  functionParameters(): object {
    return '';
  }
}
