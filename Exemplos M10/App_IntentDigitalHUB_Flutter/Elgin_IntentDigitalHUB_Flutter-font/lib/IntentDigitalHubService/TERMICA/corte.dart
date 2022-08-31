import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class Corte extends TermicaCommand {
  final int _avanco;

  Corte(this._avanco) : super('Corte');

  @override
  functionParameters() => '"avanco"' ':' '$_avanco';
}
