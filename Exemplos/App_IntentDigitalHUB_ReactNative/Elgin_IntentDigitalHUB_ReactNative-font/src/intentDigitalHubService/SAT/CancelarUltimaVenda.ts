import {SatCommand} from './SatCommand';

export class CancelarUltimaVenda extends SatCommand {
  readonly numSessao: number;
  readonly codAtivacao: string;
  readonly numeroCFe: string;
  readonly dadosCancelamento: string;

  constructor(
    numSessao: number,
    codAtivacao: string,
    numeroCFe: string,
    dadosCancelamento: string,
  ) {
    super('CancelarUltimaVenda');
    this.numSessao = numSessao;
    this.codAtivacao = codAtivacao;
    this.numeroCFe = numeroCFe;
    this.dadosCancelamento = dadosCancelamento;
  }

  functionParameters(): object {
    return {
      numSessao: this.numSessao,
      codAtivacao: this.codAtivacao,
      numeroCFe: this.numeroCFe,
      dadosCancelamento: this.dadosCancelamento,
    };
  }
}
