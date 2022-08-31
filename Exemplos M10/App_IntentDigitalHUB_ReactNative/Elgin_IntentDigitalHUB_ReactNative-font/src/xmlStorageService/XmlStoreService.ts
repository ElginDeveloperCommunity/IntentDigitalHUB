import XmlFile from './xmlFile';
import NativeServiceInterface from '../wrapperIdhNativeModule';

/**
 * Classe service que salvará todos os XMLs do projeto dentro do diretório raiz da aplicação (/Android/data/..)
 */
export default class XmlStoreService {
  //Executa o salvamento de todos os xmls
  public static allocateXmls(): void {
    let xmlFiles: Array<XmlFile> = [
      XmlFile.XML_NFCE,
      XmlFile.XML_SAT,
      XmlFile.XML_SAT_CANCELAMENTO,
      XmlFile.SAT_ENVIAR_DADOS_VENDA,
      XmlFile.SAT_GO_ENVIAR_DADOS_VENDA,
    ];

    for (let xmlFile of xmlFiles) {
      NativeServiceInterface.storeXmlFile(
        xmlFile.XmlContentInString,
        xmlFile.xmlArchiveName,
      );
    }
  }
}
