import {BridgeCommand} from './BridgeCommand';

export class ImprimirCupomSatCancelamento extends BridgeCommand {
  readonly xml: string;
  readonly assQRCode: string;

  constructor(xml: string, assQRCode: string) {
    super('ImprimirCupomSatCancelamento');
    this.xml = xml;
    this.assQRCode = assQRCode;
  }

  functionParameters(): object {
    return {xml: this.xml, assQRCode: this.assQRCode};
  }
}
