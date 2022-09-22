import {ElginPayCommand} from '../ElginPayCommand';

export class IniciaOperacaoAdministrativa extends ElginPayCommand {
  constructor() {
    super('iniciaOperacaoAdministrativa');
  }

  functionParameters(): object {
    return {};
  }
}
