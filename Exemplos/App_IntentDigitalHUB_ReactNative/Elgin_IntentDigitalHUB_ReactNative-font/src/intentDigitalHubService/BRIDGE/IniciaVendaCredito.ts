import {BridgeCommand} from './BridgeCommand';

export class IniciaVendaCredito extends BridgeCommand {
  readonly idTransacao: number;
  readonly pdv: string;
  readonly valorTotal: string;
  readonly tipoFinanciamento: number;
  readonly numeroParcelas: number;

  constructor(
    idTransacao: number,
    pdv: string,
    valorTotal: string,
    tipoFinanciamento: number,
    numeroParcelas: number,
  ) {
    super('IniciaVendaCredito');
    this.idTransacao = idTransacao;
    this.pdv = pdv;
    this.valorTotal = valorTotal;
    this.tipoFinanciamento = tipoFinanciamento;
    this.numeroParcelas = numeroParcelas;
  }

  functionParameters(): object {
    return {
      idTransacao: this.idTransacao,
      pdv: this.pdv,
      valorTotal: this.valorTotal,
      tipoFinanciamento: this.tipoFinanciamento,
      numeroParcelas: this.numeroParcelas,
    };
  }
}
