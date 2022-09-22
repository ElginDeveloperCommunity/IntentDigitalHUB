import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/termica_command.dart';

class Corte extends TermicaCommand {
  final int _avanco;

  Corte(this._avanco) : super('Corte');

  @override
  get functionParametersJson => {
        'avanco': _avanco,
      };
}
