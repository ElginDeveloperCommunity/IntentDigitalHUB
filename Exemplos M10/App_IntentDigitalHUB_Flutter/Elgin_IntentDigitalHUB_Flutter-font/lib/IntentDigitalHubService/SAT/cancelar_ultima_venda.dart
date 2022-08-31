import 'package:intent_digital_hub/IntentDigitalHubService/SAT/sat_command.dart';

class CancelarUltimaVenda extends SatCommand {
  final int _numSessao;
  final String _codAtivacao;
  final String _numeroCFe;
  final String _dadosCancelamento;

  CancelarUltimaVenda(this._numSessao, this._codAtivacao, this._numeroCFe,
      this._dadosCancelamento)
      : super('CancelarUltimaVenda');

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
        '"numeroCFe"'
        ':'
        '"$_numeroCFe"'
        ','
        '"dadosCancelamento"'
        ':'
        '"'
        '$_dadosCancelamento'
        '"';
  }
}
