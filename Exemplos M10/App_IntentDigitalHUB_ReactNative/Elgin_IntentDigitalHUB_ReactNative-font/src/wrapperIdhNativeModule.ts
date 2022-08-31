/**
 * Classe que expõe o módulo nativo IntentDigitalHubNativeModule como um módulo TS, servindo como wrapper para as chamadas nativas, proporcionando typechecking e evitando declarar NativeModules várias vezes nas páginas que utilizam chamadas nativas.
 * Funções exportadas a partir de IntentDigitalHubNativeModule.java na pasta da plataforma Android
 */
import {NativeModules} from 'react-native';

//Nome do módulo registrado na plataforma Android
const {IntentDigitalHubNativeModule} = NativeModules;

//Interface que serve como Wrapper para as funções nativas utilizadas
interface NativeServiceInterface {
  //Inicia a intent do IntentDigitalHub
  startIntentForResult(
    commandJson: string,
    intentPath: string,
    callback: (result: string) => void,
  ): void;

  /**
   * Cria no diretório da aplicação, em /Android/data um arquivo xml para ser utilizado nas funções que envolvem XML, pois o uso será através por caminho
   * @param xmlContentInString Conteúdo do xml em string
   * @param xmlFileName Nome do arquivo xml
   */
  storeXmlFile(xmlContentInString: string, xmlFileName: string): void;
}

export default IntentDigitalHubNativeModule as NativeServiceInterface;
