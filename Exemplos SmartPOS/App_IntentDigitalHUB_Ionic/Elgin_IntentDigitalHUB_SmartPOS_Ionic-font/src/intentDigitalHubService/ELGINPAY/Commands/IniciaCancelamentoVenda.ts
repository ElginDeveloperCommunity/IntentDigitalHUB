import {ElginPayCommand} from '../ElginPayCommand';

export class IniciaCancelamentoVenda extends ElginPayCommand {
  readonly valorTotal: string;
  readonly data: string;
  readonly ref: string;

  constructor(valorTotal: string, data: string, ref: string) {
    super('iniciaCancelamentoVenda');
    this.valorTotal = valorTotal;
    this.data = data;
    this.ref = ref;
  }

  functionParameters(): object {
    return {
      valorTotal: this.valorTotal,
      data: this.data,
      ref: this.ref,
    };
  }
}
