import NativeServiceInterface from '../wrapperIdhNativeModule';
import {IntentDigitalHubCommand} from './IntentDigitalHubCommand';

/**
 * Classe service utilizada para iniciar os comandos do Intent Digital Hub
 */
export default class IntentDigitalHubCommandStarter {
  //Classe service, não deve ser possível instânciar
  private constructor() {}

  /**
   * Inicia um comando do IDH, faz chamadas ao módulo nativo (Android) para executar as intents-comando que são formatadas pelas classes implementadas
   * @param intentDigitalHubCommand Uma classe que implementa IntentDigitalHubCommand, ou seja, um comando válido para o IDH
   * @param handleCallbackFunction Função que consumirá o retorno da intent
   */
  public static startCommand(
    intentDigitalHubCommand: IntentDigitalHubCommand,
    handleCallbackFunction: (result: string) => void,
  ) {
    const commandArray = [];
    commandArray.push(intentDigitalHubCommand._getCommandJSON());

    NativeServiceInterface.startIntentForResult(
      JSON.stringify(commandArray),
      intentDigitalHubCommand.correspondingIntentModule,
      handleCallbackFunction,
    );
  }

  /**
   * Como o comando anterior, porém inicia uma lista de vários comandos
   * @param intentDigitalHubCommands Uma lista de classes que implementam IntentDigitalHubCommand, ou seja, vários comandos válidos para o IDH (Para que a lista de comando final forme um comando válido, todos os comandos devem pertencer ao mesmo módulo, isto é validado na função)
   * @param handleCallbackFunction Função que consumiŕa o retorno da intent
   */
  public static startCommands(
    intentDigitalHubCommands: Array<IntentDigitalHubCommand>,
    handleCallbackFunction: (result: string) => void,
  ) {
    //Valida se a lista de comandos não está vazia
    if (intentDigitalHubCommands.length === 0) {
      throw new Error(
        'A lista de comandos a serem concatenadas não pode estar vazia!',
      );
    }

    //Valida se todos os comandos da lista pertencem ao mesmo módulo
    if (!this.validateCommandList(intentDigitalHubCommands)) {
      throw new Error(
        'Todos os comandos da lista devem pertencer ao mesmo módulo!',
      );
    }

    //Cria o comando contendo todos os comandos no array
    const commandJson: string = this.getConcatenatedDigitalHubCommand(
      intentDigitalHubCommands,
    );

    //Módulo dos comandos da lista
    const digitalHubIntentModule: string =
      intentDigitalHubCommands[0].correspondingIntentModule;

    NativeServiceInterface.startIntentForResult(
      commandJson,
      digitalHubIntentModule,
      handleCallbackFunction,
    );
  }

  //Valida se os commandos da lista são todos pertecentes ao mesmo módulo
  private static validateCommandList(
    intentDigitalHubCommands: Array<IntentDigitalHubCommand>,
  ): boolean {
    //Captura o módulo do primeiro comando
    const digitalHubIntentBase =
      intentDigitalHubCommands[0].correspondingIntentModule;

    intentDigitalHubCommands.forEach(intentDigitalHubCommand => {
      if (
        intentDigitalHubCommand.correspondingIntentModule !==
        digitalHubIntentBase
      ) {
        return false;
      }
    });

    return true;
  }

  //Concatena os comandos em uma lista, criando um JSONArray, com todos os comandos concatenados em sequência
  private static getConcatenatedDigitalHubCommand(
    intentDigitalHubCommands: Array<IntentDigitalHubCommand>,
  ): string {
    const commandArray = [];

    for (let i = 0; i < intentDigitalHubCommands.length; i++) {
      commandArray.push(intentDigitalHubCommands[i]._getCommandJSON());
    }

    // Converte o array de objetos em string
    return JSON.stringify(commandArray);
  }
}
