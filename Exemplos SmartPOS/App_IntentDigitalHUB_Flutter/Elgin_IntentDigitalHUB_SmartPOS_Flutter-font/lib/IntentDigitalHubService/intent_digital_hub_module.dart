// ignore: constant_identifier_names
enum IntentDigitalHubModule { ELGINPAY, TERMICA, SCANNER }

extension DigitalHubExtension on IntentDigitalHubModule {
  String get intentPath {
    switch (this) {
      case IntentDigitalHubModule.ELGINPAY:
        return 'com.elgin.e1.digitalhub.ELGINPAY';
      case IntentDigitalHubModule.TERMICA:
        return 'com.elgin.e1.digitalhub.TERMICA';
      case IntentDigitalHubModule.SCANNER:
        return 'com.elgin.e1.digitalhub.SCANNER';
    }
  }
}
