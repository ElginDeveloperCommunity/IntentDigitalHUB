import 'package:intent_digital_hub/IntentDigitalHubService/SAT/sat_command.dart';

class EnviarDadosVenda extends SatCommand {
  final int _numSessao;
  final String _codAtivacao;
  final String _dadosVenda;

  EnviarDadosVenda(this._numSessao, this._codAtivacao, this._dadosVenda)
      : super('EnviarDadosVenda');

  @override
  functionParameters() {
    return '"numSessao"'
        ':'
        '$_numSessao'
        ','
        '"codAtivacao"'
        ':'
        '"$_codAtivacao"'
        ','
        '"dadosVenda"'
        ':'
        '"'
        '$_dadosVenda'
        '"';
  }
}
