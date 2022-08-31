import { BridgeCommand } from "../BridgeCommand";

export class ImprimirCupomSat extends BridgeCommand {
  readonly xml: string;

  constructor(xml: string) {
    super("ImprimirCupomSat");
    this.xml = xml;
  }

  functionParameters(): object {
    return { xml: this.xml };
  }
}
