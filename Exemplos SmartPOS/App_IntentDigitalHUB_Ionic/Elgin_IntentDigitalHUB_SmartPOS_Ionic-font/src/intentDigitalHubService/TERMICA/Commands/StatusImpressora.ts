import {TermicaCommand} from '../TermicaCommand';

export class StatusImpressora extends TermicaCommand {
  readonly param: number;

  constructor(param: number) {
    super('StatusImpressora');
    this.param = param;
  }

  functionParameters(): object {
    return {param: this.param};
  }
}
