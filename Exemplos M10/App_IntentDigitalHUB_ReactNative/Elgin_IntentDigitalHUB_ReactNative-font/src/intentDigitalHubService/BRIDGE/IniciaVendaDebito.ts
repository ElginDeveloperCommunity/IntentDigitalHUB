import {BridgeCommand} from './BridgeCommand';

export class IniciaVendaDebito extends BridgeCommand {
  readonly idTransacao: number;
  readonly pdv: string;
  readonly valorTotal: string;

  constructor(idTransacao: number, pdv: string, valorTotal: string) {
    super('IniciaVendaDebito');
    this.idTransacao = idTransacao;
    this.pdv = pdv;
    this.valorTotal = valorTotal;
  }

  functionParameters(): object {
    return {
      idTransacao: this.idTransacao,
      pdv: this.pdv,
      valorTotal: this.valorTotal,
    };
  }
}
