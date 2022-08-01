import XmlFile from "./XmlFile";
import NativeServiceInterface from "../Wrapper_idh_capacitor_methods";

/**
 * Classe service que salvará todos os XMLs do projeto dentro do diretório raiz da aplicação (/Android/data/..)
 */
export default class XmlStoreService {
  //Executa o salvamento de todos os xmls
  public static async allocateXmls(): Promise<void> {
    //Captura todos os XMLs que serão utilizados na aplicação
    let xmlFiles: Array<XmlFile> = XmlFile.getAllXmls();

    for (let xmlFile of xmlFiles) {
      await xmlFile.getXmlContentInString().then(async (content) => {
        await NativeServiceInterface.storeXmlFile({
          xmlContentInString: content,
          xmlFileName: xmlFile.getXmlFileName(),
        });
      });
    }
  }
}
