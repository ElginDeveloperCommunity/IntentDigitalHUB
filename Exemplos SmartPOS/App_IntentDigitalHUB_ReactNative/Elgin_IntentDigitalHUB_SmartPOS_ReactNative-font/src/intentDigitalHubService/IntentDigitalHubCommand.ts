import {IntentDigitalHubModule} from './IntentDigitalHubModule';

/**
 * Classe abstrata que generaliza as carecterísticas comuns à todos os comandos do Intent Digital Hub, as classes que herdam desta implementam cada comando de maneira específica servindo como Wrapper
 * para todas as funções
 */

export abstract class IntentDigitalHubCommand {
  //Nome da função
  functionName: string;
  //Módulo ao qual a função pertence
  correspondingIntentModule: IntentDigitalHubModule;

  constructor(
    functionName: string,
    correspondingIntentModule: IntentDigitalHubModule,
  ) {
    this.functionName = functionName;
    this.correspondingIntentModule = correspondingIntentModule;
  }

  //Formata o JSON de acordo com os parâmetros definidos por cada subclasse, o modificador de acesso protected impede a exposição do método que sera usado somente em DigitalHubUtils para o start da intent
  _getCommandJSON() {
    return {
      funcao: this.functionName,
      parametros: this.functionParameters(),
    };
  }

  abstract functionParameters(): object;
}
