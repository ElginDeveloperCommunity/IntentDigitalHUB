import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/termica_command.dart';

class AvancaPapel extends TermicaCommand {
  final int _linhas;

  AvancaPapel(this._linhas) : super('AvancaPapel');

  @override
  get functionParametersJson => {
        'linhas': _linhas,
      };
}
