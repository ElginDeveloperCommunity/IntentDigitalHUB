import {ElginPayCommand} from '../ElginPayCommand';

export class IniciaVendaDebito extends ElginPayCommand {
  readonly valorTotal: string;

  constructor(valorTotal: string) {
    super('iniciaVendaDebito');
    this.valorTotal = valorTotal;
  }

  functionParameters(): object {
    return {
      valorTotal: this.valorTotal,
    };
  }
}
