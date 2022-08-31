import { IntentDigitalHubCommand } from "../IntentDigitalHubCommand";
import { IntentDigitalHubModule } from "../IntentDigitalHubModule";

/**
 * Classe que generaliza todos os comandos SAT definindo o módulo do comando e o seu tipo de retorno
 */
export abstract class SatCommand extends IntentDigitalHubCommand {
  constructor(functionName: string) {
    super(functionName, IntentDigitalHubModule.SAT);
  }
}
