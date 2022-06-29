import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class ImpressaoQRCode extends TermicaCommand {
  final String _dados;
  final int _tamanho;
  final int _nivelCorrecao;

  ImpressaoQRCode(this._dados, this._tamanho, this._nivelCorrecao)
      : super('ImpressaoQRCode');

  @override
  functionParameters() {
    return '"dados"'
        ':'
        '"$_dados"'
        ','
        '"tamanho"'
        ':'
        '$_tamanho'
        ','
        '"nivelCorrecao"'
        ':'
        '$_nivelCorrecao';
  }
}
