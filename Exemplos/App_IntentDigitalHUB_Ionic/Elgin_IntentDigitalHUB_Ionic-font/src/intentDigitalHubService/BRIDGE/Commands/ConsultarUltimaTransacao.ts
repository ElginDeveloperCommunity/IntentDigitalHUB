import { BridgeCommand } from "../BridgeCommand";

export class ConsultarUltimaTransacao extends BridgeCommand {
  readonly pdv: string;

  constructor(pdv: string) {
    super("ConsultarUltimaTransacao");
    this.pdv = pdv;
  }

  functionParameters(): object {
    return { pdv: this.pdv };
  }
}
