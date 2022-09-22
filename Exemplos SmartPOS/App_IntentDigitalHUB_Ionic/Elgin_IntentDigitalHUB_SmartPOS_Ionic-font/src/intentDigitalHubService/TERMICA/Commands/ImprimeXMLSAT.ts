import {TermicaCommand} from '../TermicaCommand';

export class ImprimeXMLSAT extends TermicaCommand {
  readonly dados: string;
  readonly param: number;

  constructor(dados: string, param: number) {
    super('ImprimeXMLSAT');
    this.dados = dados;
    this.param = param;
  }

  functionParameters(): object {
    return {dados: this.dados, param: this.param};
  }
}
