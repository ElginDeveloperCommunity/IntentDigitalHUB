import { TermicaCommand } from "../TermicaCommand";

export class ImpressaoTexto extends TermicaCommand {
  readonly dados: string;
  readonly posicao: number;
  readonly stilo: number;
  readonly tamanho: number;

  constructor(dados: string, posicao: number, stilo: number, tamanho: number) {
    super("ImpressaoTexto");
    this.dados = dados;
    this.posicao = posicao;
    this.stilo = stilo;
    this.tamanho = tamanho;
  }

  functionParameters(): object {
    return {
      dados: this.dados,
      posicao: this.posicao,
      stilo: this.stilo,
      tamanho: this.tamanho,
    };
  }
}
