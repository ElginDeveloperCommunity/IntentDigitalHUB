import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class AvancaPapel extends TermicaCommand {
  final int _linhas;

  AvancaPapel(this._linhas) : super('AvancaPapel');

  @override
  functionParameters() => '"linhas"' ':' '$_linhas';
}
