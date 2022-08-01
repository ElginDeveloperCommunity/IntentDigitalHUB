import { BridgeCommand } from "../BridgeCommand";

export class IniciaOperacaoAdministrativa extends BridgeCommand {
  readonly idTransacao: number;
  readonly pdv: string;
  readonly operacao: number;

  constructor(idTransacao: number, pdv: string, operacao: number) {
    super("IniciaOperacaoAdministrativa");
    this.idTransacao = idTransacao;
    this.pdv = pdv;
    this.operacao = operacao;
  }

  functionParameters(): object {
    return {
      idTransacao: this.idTransacao,
      pdv: this.pdv,
      operacao: this.operacao,
    };
  }
}
