import {ElginPayCommand} from '../ElginPayCommand';

export class IniciaVendaCredito extends ElginPayCommand {
  readonly valorTotal: string;
  readonly tipoFinanciamento: number;
  readonly numeroParcelas: number;

  constructor(
    valorTotal: string,
    tipoFinanciamento: number,
    numeroParcelas: number,
  ) {
    super('iniciaVendaCredito');
    this.valorTotal = valorTotal;
    this.tipoFinanciamento = tipoFinanciamento;
    this.numeroParcelas = numeroParcelas;
  }

  functionParameters(): object {
    return {
      valorTotal: this.valorTotal,
      tipoFinanciamento: this.tipoFinanciamento,
      numeroParcelas: this.numeroParcelas,
    };
  }
}
