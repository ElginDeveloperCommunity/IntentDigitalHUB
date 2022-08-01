import { TermicaCommand } from "../TermicaCommand";

export class AbreConexaoImpressora extends TermicaCommand {
  readonly tipo: number;
  readonly modelo: string;
  readonly conexao: string;
  readonly parametro: number;

  constructor(
    tipo: number,
    modelo: string,
    conexao: string,
    parametro: number
  ) {
    super("AbreConexaoImpressora");
    this.tipo = tipo;
    this.modelo = modelo;
    this.conexao = conexao;
    this.parametro = parametro;
  }

  functionParameters(): object {
    return {
      tipo: this.tipo,
      modelo: this.modelo,
      conexao: this.conexao,
      parametro: this.parametro,
    };
  }
}
