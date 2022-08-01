import {SatCommand} from './SatCommand';

export class ExtrairLogs extends SatCommand {
  readonly numSessao: number;
  readonly codAtivacao: string;

  constructor(numSessao: number, codAtivacao: string) {
    super('ExtrairLogs');
    this.numSessao = numSessao;
    this.codAtivacao = codAtivacao;
  }

  functionParameters(): object {
    return {numSessao: this.numSessao, codAtivacao: this.codAtivacao};
  }
}
