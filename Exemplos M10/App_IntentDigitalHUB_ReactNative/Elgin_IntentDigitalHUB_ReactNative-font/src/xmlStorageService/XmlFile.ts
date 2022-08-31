import SatGoXmlEnviarDadosVenda from '../rawXmls/satgo_enviar_dados_venda';
import SatXmlEnviarDadosVenda from '../rawXmls/sat_enviar_dados_venda';
import XmlNfce from '../rawXmls/xmlnfce';
import XmlSat from '../rawXmls/xmlsat';
import XmlSatCancelamento from '../rawXmls/xmlsatcancelamento';

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
  public static readonly XML_SAT_CANCELAMENTO: XmlFile = new XmlFile(
    new XmlSatCancelamento().getXml(),
    'xmlsatcancelamento',
  );
  //Xmls usados no módulo SAT
  public static readonly SAT_ENVIAR_DADOS_VENDA: XmlFile = new XmlFile(
    new SatXmlEnviarDadosVenda().getXml(),
    'sat_enviar_dados_venda',
  );
  public static readonly SAT_GO_ENVIAR_DADOS_VENDA: XmlFile = new XmlFile(
    new SatGoXmlEnviarDadosVenda().getXml(),
    'satgo_enviar_dados_venda',
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
