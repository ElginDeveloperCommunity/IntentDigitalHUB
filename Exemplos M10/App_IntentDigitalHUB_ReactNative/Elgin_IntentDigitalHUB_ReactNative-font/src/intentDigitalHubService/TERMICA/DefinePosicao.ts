import {TermicaCommand} from './TermicaCommand';

export class DefinePosicao extends TermicaCommand {
  readonly posicao: number;

  constructor(posicao: number) {
    super('DefinePosicao');
    this.posicao = posicao;
  }

  functionParameters(): object {
    return {posicao: this.posicao};
  }
}
