import { BridgeCommand } from "../BridgeCommand";

export class SetSenhaServer extends BridgeCommand {
  readonly senha: string;
  readonly habilitada: boolean;

  constructor(senha: string, habilitada: boolean) {
    super("SetSenhaServer");
    this.senha = senha;
    this.habilitada = habilitada;
  }

  functionParameters(): object {
    return { senha: this.senha, habilitada: this.habilitada };
  }
}
