import {TermicaCommand} from '../TermicaCommand';

export class ImpressaoCodigoBarras extends TermicaCommand {
  readonly tipo: number;
  readonly dados: string;
  readonly altura: number;
  readonly largura: number;
  readonly HRI: number;

  constructor(
    tipo: number,
    dados: string,
    altura: number,
    largura: number,
    HRI: number,
  ) {
    super('ImpressaoCodigoBarras');
    this.tipo = tipo;
    this.dados = dados;
    this.altura = altura;
    this.largura = largura;
    this.HRI = HRI;
  }

  functionParameters(): object {
    return {
      tipo: this.tipo,
      dados: this.dados,
      altura: this.altura,
      largura: this.largura,
      HRI: this.HRI,
    };
  }
}
