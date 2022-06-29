import 'package:intent_digital_hub/IntentDigitalHubService/SAT/sat_command.dart';

class ConsultarStatusOperacional extends SatCommand {
  final int _numSessao;
  final String _codAtivacao;

  ConsultarStatusOperacional(this._numSessao, this._codAtivacao)
      : super('ConsultarStatusOperacional');

  @override
  functionParameters() {
    return '"numSessao"'
        ':'
        '$_numSessao'
        ','
        '"codAtivacao"'
        ':'
        '"$_codAtivacao"';
  }
}
