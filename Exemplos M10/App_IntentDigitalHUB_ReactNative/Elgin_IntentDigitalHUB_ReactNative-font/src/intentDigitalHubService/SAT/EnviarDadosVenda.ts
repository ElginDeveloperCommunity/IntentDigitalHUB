import {SatCommand} from './SatCommand';

export class EnviarDadosVenda extends SatCommand {
  readonly numSessao: number;
  readonly codAtivacao: string;
  readonly dadosVenda: string;

  constructor(numSessao: number, codAtivacao: string, dadosVenda: string) {
    super('EnviarDadosVenda');
    this.numSessao = numSessao;
    this.codAtivacao = codAtivacao;
    this.dadosVenda = dadosVenda;
  }

  functionParameters(): object {
    return {
      numSessao: this.numSessao,
      codAtivacao: this.codAtivacao,
      dadosVenda: this.dadosVenda,
    };
  }
}
