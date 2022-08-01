import { SatCommand } from "../SatCommand";

export class ConsultarStatusOperacional extends SatCommand {
  readonly numSessao: number;
  readonly codAtivacao: string;

  constructor(numSessao: number, codAtivacao: string) {
    super("ConsultarStatusOperacional");
    this.numSessao = numSessao;
    this.codAtivacao = codAtivacao;
  }

  functionParameters(): object {
    return { numSessao: this.numSessao, codAtivacao: this.codAtivacao };
  }
}
