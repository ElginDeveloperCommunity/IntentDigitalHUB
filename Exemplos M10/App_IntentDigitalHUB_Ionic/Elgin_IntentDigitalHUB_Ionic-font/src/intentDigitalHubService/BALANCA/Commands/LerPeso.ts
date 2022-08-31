import {BalancaCommand} from '../BalancaCommand';

export class LerPeso extends BalancaCommand {
  readonly qtdLeituras: number;

  constructor(qtdLeituras: number) {
    super('LerPeso');
    this.qtdLeituras = qtdLeituras;
  }

  functionParameters(): object {
    return {
      qtdLeituras: this.qtdLeituras,
    };
  }
}
