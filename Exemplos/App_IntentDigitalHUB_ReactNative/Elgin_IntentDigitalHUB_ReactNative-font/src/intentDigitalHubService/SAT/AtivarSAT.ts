import {SatCommand} from './SatCommand';

export class AtivarSAT extends SatCommand {
  readonly numSessao: number;
  readonly subComando: number;
  readonly codAtivacao: string;
  readonly cnpj: string;
  readonly cUF: number;

  constructor(
    numSessao: number,
    subComando: number,
    codAtivacao: string,
    cnpj: string,
    cUF: number,
  ) {
    super('AtivarSAT');
    this.numSessao = numSessao;
    this.subComando = subComando;
    this.codAtivacao = codAtivacao;
    this.cnpj = cnpj;
    this.cUF = cUF;
  }

  functionParameters(): object {
    return {
      numSessao: this.numSessao,
      subComando: this.subComando,
      codAtivacao: this.codAtivacao,
      cnpj: this.cnpj,
      cUF: this.cUF,
    };
  }
}
