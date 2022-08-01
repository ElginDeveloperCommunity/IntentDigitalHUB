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

  //XMls utilizados em impressão de cupom no módulo Bridge e Térmica.
  public static readonly XML_NFCE: XmlFile = new XmlFile("xmlnfce");

  public static readonly XML_SAT: XmlFile = new XmlFile("xmlsat");

  public static readonly XML_SAT_CANCELAMENTO: XmlFile = new XmlFile(
    "xmlsatcancelamento"
  );

  public static readonly SAT_CANCELAMENTO: XmlFile = new XmlFile(
    "sat_cancelamento"
  );

  //Xmls exemplos de venda utilizados no módulo Sat.
  public static readonly SAT_ENVIAR_DADOS_VENDA: XmlFile = new XmlFile(
    "sat_enviar_dados_venda"
  );

  public static readonly SAT_GO_ENVIAR_DADOS_VENDA: XmlFile = new XmlFile(
    "satgo_enviar_dados_venda"
  );

  public static readonly ASS_QR_CODE_SAT_CANCELAMENTO: string =
    "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

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
      XmlFile.XML_SAT_CANCELAMENTO,
      XmlFile.SAT_ENVIAR_DADOS_VENDA,
      XmlFile.SAT_GO_ENVIAR_DADOS_VENDA,
    ];
  }
}
