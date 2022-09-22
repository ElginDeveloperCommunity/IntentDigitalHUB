import {TermicaCommand} from '../TermicaCommand';

export class FechaConexaoImpressora extends TermicaCommand {
  constructor() {
    super('FechaConexaoImpressora');
  }

  functionParameters(): object {
    return {};
  }
}
