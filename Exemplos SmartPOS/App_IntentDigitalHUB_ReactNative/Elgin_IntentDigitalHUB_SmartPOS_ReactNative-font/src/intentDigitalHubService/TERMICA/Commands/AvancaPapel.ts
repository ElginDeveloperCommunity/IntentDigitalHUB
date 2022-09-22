import {TermicaCommand} from '../TermicaCommand';

export class AvancaPapel extends TermicaCommand {
  readonly linhas: number;

  constructor(linhas: number) {
    super('AvancaPapel');
    this.linhas = linhas;
  }

  functionParameters(): object {
    return {linhas: this.linhas};
  }
}
