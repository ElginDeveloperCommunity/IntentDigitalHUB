import {TermicaCommand} from './TermicaCommand';

export class ImprimeXMLNFCe extends TermicaCommand {
  readonly dados: string;
  readonly indexcsc: number;
  readonly csc: string;
  readonly param: number;

  constructor(dados: string, indexcsc: number, csc: string, param: number) {
    super('ImprimeXMLNFCe');
    this.dados = dados;
    this.indexcsc = indexcsc;
    this.csc = csc;
    this.param = param;
  }

  functionParameters(): object {
    return {
      dados: this.dados,
      indexcsc: this.indexcsc,
      csc: this.csc,
      param: this.param,
    };
  }
}
