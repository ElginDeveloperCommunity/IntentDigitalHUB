import {TermicaCommand} from '../TermicaCommand';

export class ImpressaoQRCode extends TermicaCommand {
  readonly dados: string;
  readonly tamanho: number;
  readonly nivelCorrecao: number;

  constructor(dados: string, tamanho: number, nivelCorrecao: number) {
    super('ImpressaoQRCode');
    this.dados = dados;
    this.tamanho = tamanho;
    this.nivelCorrecao = nivelCorrecao;
  }

  functionParameters(): object {
    return {
      dados: this.dados,
      tamanho: this.tamanho,
      nivelCorrecao: this.nivelCorrecao,
    };
  }
}
