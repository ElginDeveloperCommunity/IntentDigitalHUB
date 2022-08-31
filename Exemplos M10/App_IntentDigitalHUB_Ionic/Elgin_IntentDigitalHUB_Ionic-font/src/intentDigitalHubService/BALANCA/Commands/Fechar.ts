import {BalancaCommand} from '../BalancaCommand';

export class Fechar extends BalancaCommand {
  constructor() {
    super('Fechar');
  }

  functionParameters(): object {
    return {};
  }
}
