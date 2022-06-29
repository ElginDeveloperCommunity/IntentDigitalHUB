import 'package:intent_digital_hub/IntentDigitalHubService/SAT/sat_command.dart';

class ExtrairLogs extends SatCommand {
  final int _numSessao;
  final String _codAtivacao;

  ExtrairLogs(this._numSessao, this._codAtivacao) : super('ExtrairLogs');

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
