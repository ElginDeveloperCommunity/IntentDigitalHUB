import { BridgeCommand } from "../BridgeCommand";

export class ConsultarStatus extends BridgeCommand {
  constructor() {
    super("ConsultarStatus");
  }

  functionParameters(): object {
    return {};
  }
}
