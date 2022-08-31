import {IntentDigitalHubCommand} from '../IntentDigitalHubCommand';
import {IntentDigitalHubModule} from '../IntentDigitalHubModule';

/**
 * Classe que generaliza todos os comandos do módulo TERMICA, definindo o módulo do comando e o seu tipo de retorno
 */
export abstract class TermicaCommand extends IntentDigitalHubCommand {
  constructor(functionName: string) {
    super(functionName, IntentDigitalHubModule.TERMICA);
  }
}
