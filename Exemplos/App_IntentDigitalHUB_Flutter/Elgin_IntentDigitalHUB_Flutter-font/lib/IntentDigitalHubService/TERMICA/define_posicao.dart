import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class DefinePosicao extends TermicaCommand {
  final int _posicao;

  DefinePosicao(this._posicao) : super('DefinePosicao');

  @override
  functionParameters() => '"posicao"' ':' '$_posicao';
}
