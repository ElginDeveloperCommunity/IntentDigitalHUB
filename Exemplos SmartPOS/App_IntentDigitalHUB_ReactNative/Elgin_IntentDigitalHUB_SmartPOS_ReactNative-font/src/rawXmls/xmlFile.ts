import XmlNfce from './xmlnfce';
import XmlSat from './xmlsat';

/**
 * Classe que empacota todos argumentos necessários para a criação dos xmls no diretório da aplicação
 */
export default class XmlFile {
  //Xmls usados nos módulos de impressão de cupom bridge e na impressão de nfce e sat, no módulo de impressão de texto.
  public static readonly XML_NFCE: XmlFile = new XmlFile(
    new XmlNfce().getXml(),
    'xmlnfce',
  );
  public static readonly XML_SAT: XmlFile = new XmlFile(
    new XmlSat().getXml(),
    'xmlsat',
  );

  /**
   * @param XmlContentInString XMLs salvas no projeto em formato String, (../rawXmls/) ; utilizadas para requisitar ao nativo que salve o arquivo no diretório raiz da aplicação
   * @param xmlArchiveName Nome que o arquivo terá ao ser salvo no diretório raiz da aplicação
   */
  private constructor(
    public readonly XmlContentInString: string,
    public readonly xmlArchiveName: string,
  ) {}
}
