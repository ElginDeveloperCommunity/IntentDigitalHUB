import {TermicaCommand} from '../TermicaCommand';

export class Corte extends TermicaCommand {
  readonly avanco: number;

  constructor(avanco: number) {
    super('Corte');
    this.avanco = avanco;
  }

  functionParameters(): object {
    return {avanco: this.avanco};
  }
}
