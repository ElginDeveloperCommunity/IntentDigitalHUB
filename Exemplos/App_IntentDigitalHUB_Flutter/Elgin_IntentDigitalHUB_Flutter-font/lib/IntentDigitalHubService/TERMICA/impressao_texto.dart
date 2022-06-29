import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class ImpressaoTexto extends TermicaCommand {
  final String _dados;
  final int _posicao;
  final int _stilo;
  final int _tamanho;

  ImpressaoTexto(this._dados, this._posicao, this._stilo, this._tamanho)
      : super('ImpressaoTexto');

  @override
  functionParameters() {
    return '"dados"'
        ':'
        '"$_dados"'
        ','
        '"posicao"'
        ':'
        '$_posicao'
        ','
        '"stilo"'
        ':'
        '$_stilo'
        ','
        '"tamanho"'
        ':'
        '$_tamanho';
  }
}
