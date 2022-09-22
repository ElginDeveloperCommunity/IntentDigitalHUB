export default class XmlFile {
  /**
   * * @param xmlFileName Nome corresponde do arquivo em assets/RawXmls. */
  private constructor(public readonly xmlFileName: string) {
    this.xmlContentInString = XmlFile.readXmlAsString(xmlFileName);
  }

  /**
   * @param XmlContentInString Conteúdo do arquivo XML que será lido e salvo no construtor. */
  private readonly xmlContentInString;

  //Realiza a leitura do arquivo XML em formato string
  static async readXmlAsString(xmlAssetName: string): Promise<string> {
    let xmlAsString: string = await fetch(
      `/assets/RawXmls/${xmlAssetName}.xml`
    ).then((response) => response.text());

    return xmlAsString.toString();
  }

  //XMls utilizados em impressão de cupom no módulo Térmica.
  public static readonly XML_NFCE: XmlFile = new XmlFile("xmlnfce");

  public static readonly XML_SAT: XmlFile = new XmlFile("xmlsat");

  //Retorna o conteúdo do xml em string.
  public getXmlContentInString(): Promise<string> {
    return this.xmlContentInString;
  }

  //Retorna o nome do arquivo xml
  public getXmlFileName(): string {
    return this.xmlFileName;
  }

  //Retorna um vetor com todos os XmlFiles
  public static getAllXmls(): Array<XmlFile> {
    return [
      XmlFile.XML_NFCE,
      XmlFile.XML_SAT,
    ];
  }
}
