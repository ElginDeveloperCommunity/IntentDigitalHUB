import {BalancaCommand} from './BalancaCommand';

export class ConfigurarModeloBalanca extends BalancaCommand {
  readonly modeloBalanca: number;

  constructor(modeloBalanca: number) {
    super('ConfigurarModeloBalanca');
    this.modeloBalanca = modeloBalanca;
  }

  functionParameters(): object {
    return {
      modeloBalanca: this.modeloBalanca,
    };
  }
}
