import { SatCommand } from "../SatCommand";

export class AssociarAssinatura extends SatCommand {
  readonly numSessao: number;
  readonly codAtivacao: string;
  readonly cnpjSH: string;
  readonly assinaturaAC: string;

  constructor(
    numSessao: number,
    codAtivacao: string,
    cnpjSH: string,
    assinaturaAC: string
  ) {
    super("AssociarAssinatura");
    this.numSessao = numSessao;
    this.codAtivacao = codAtivacao;
    this.cnpjSH = cnpjSH;
    this.assinaturaAC = assinaturaAC;
  }

  functionParameters(): object {
    return {
      numSessao: this.numSessao,
      codAtivacao: this.codAtivacao,
      cnpjSH: this.cnpjSH,
      assinaturaAC: this.assinaturaAC,
    };
  }
}
