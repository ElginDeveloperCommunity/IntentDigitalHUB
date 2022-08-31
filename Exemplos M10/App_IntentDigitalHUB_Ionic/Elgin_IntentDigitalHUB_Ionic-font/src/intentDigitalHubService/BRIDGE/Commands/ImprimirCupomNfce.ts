import { BridgeCommand } from "../BridgeCommand";

export class ImprimirCupomNfce extends BridgeCommand {
  readonly xml: string;
  readonly indexcsc: number;
  readonly csc: string;

  constructor(xml: string, indexcsc: number, csc: string) {
    super("ImprimirCupomNfce");
    this.xml = xml;
    this.indexcsc = indexcsc;
    this.csc = csc;
  }

  functionParameters(): object {
    return { xml: this.xml, indexcsc: this.indexcsc, csc: this.csc };
  }
}
