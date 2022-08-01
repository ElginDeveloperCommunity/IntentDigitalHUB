import {IntentDigitalHubCommand} from '../IntentDigitalHubCommand';
import {IntentDigitalHubModule} from '../IntentDigitalHubModule';

/**
 * Classe que generaliza todos os comandos Balanca definindo o módulo do comando e o seu tipo de retorno
 */
export abstract class BalancaCommand extends IntentDigitalHubCommand {
  constructor(functionName: string) {
    super(functionName, IntentDigitalHubModule.BALANCA);
  }
}
