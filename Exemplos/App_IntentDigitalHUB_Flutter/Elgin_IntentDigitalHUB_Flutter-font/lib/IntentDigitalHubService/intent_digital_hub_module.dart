// ignore: constant_identifier_names
enum IntentDigitalHubModule { BALANCA, BRIDGE, TERMICA, SAT }

extension DigitalHubExtension on IntentDigitalHubModule {
  String get intentPath {
    switch (this) {
      case IntentDigitalHubModule.BALANCA:
        return 'com.elgin.e1.digitalhub.BALANCA';
      case IntentDigitalHubModule.BRIDGE:
        return 'com.elgin.e1.digitalhub.BRIDGE';
      case IntentDigitalHubModule.TERMICA:
        return 'com.elgin.e1.digitalhub.TERMICA';
      case IntentDigitalHubModule.SAT:
        return 'com.elgin.e1.digitalhub.SAT';
    }
  }
}
