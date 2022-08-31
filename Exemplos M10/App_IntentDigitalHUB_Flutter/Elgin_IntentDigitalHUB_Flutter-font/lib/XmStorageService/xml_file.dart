// ignore_for_file: constant_identifier_names

part of 'xml_database_service.dart';

enum XmlFile {
  //Xmls usados nos módulos de impressão de cupom bridge e na impressão de nfce e sat, no módulo de impressão de texto.
  XML_NFCE,
  XML_SAT,
  XML_SAT_CANCELAMENTO,
  //Xmls usados no módulo SAT
  SAT_ENVIAR_DADOS_VENDA,
  SAT_GO_ENVIAR_DADOS_VENDA
}

extension XmlFileExtension on XmlFile {
  //Formatação do caminho absoluto, usado para salvar o arquivo XML dentro do diretório da aplicação
  Future<String> get _absolutePath async {
    String? externalDirectory = (await getExternalStorageDirectory())?.path;
    return externalDirectory! + '/' + getXmlArchiveName(this);
  }

  //Formata como o argumento deve ser passado ao IntentDigitalHub; para a passagem de xml por PATH é necessário enviar
  //'path=' no começo da string, e logo após o caminho relativo iniciando a partir do diretório raiz do dispositivo
  String get idhRelativePathForCommand {
    return 'path=/Android/data/com.elgin.flutter.intent_digital_hub/files/' +
        getXmlArchiveName(this);
  }
}

//Nome corresponde dos arquivos em assets/xml
String getXmlArchiveName(XmlFile xmlFile) {
  switch (xmlFile) {
    case XmlFile.XML_NFCE:
      return 'xmlnfce.xml';
    case XmlFile.XML_SAT:
      return 'xmlsat.xml';
    case XmlFile.XML_SAT_CANCELAMENTO:
      return 'xmlsatcancelamento.xml';
    case XmlFile.SAT_ENVIAR_DADOS_VENDA:
      return 'sat_enviar_dados_venda.xml';
    case XmlFile.SAT_GO_ENVIAR_DADOS_VENDA:
      return 'satgo_enviar_dados_venda.xml';
  }
}
