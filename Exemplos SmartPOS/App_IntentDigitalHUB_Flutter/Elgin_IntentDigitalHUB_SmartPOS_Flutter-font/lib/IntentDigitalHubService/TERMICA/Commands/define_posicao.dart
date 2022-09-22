import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/termica_command.dart';

class DefinePosicao extends TermicaCommand {
  final int _posicao;

  DefinePosicao(this._posicao) : super('DefinePosicao');

  @override
  get functionParametersJson => {
        'posicao': _posicao,
      };
}
