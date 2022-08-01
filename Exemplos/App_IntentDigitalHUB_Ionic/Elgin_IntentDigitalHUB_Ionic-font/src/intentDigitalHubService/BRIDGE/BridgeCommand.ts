import { IntentDigitalHubCommand } from "../IntentDigitalHubCommand";
import { IntentDigitalHubModule } from "../IntentDigitalHubModule";

/**
 * Classe que generaliza todos os comandos do módulo BRIDGE, definindo o módulo do comando e o seu tipo de retorno
 */
export abstract class BridgeCommand extends IntentDigitalHubCommand {
  constructor(functionName: string) {
    super(functionName, IntentDigitalHubModule.BRIDGE);
  }
}
