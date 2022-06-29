import 'package:intent_digital_hub/IntentDigitalHubService/SAT/sat_command.dart';

class AtivarSAT extends SatCommand {
  final int _numSessao;
  final int _subComando;
  final String _codAtivacao;
  final String _cnpj;
  final int _cUF;

  AtivarSAT(this._numSessao, this._subComando, this._codAtivacao, this._cnpj,
      this._cUF)
      : super('AtivarSAT');

  @override
  functionParameters() {
    return '"numSessao"'
        ':'
        '$_numSessao'
        ','
        '"subComando"'
        ':'
        '$_subComando'
        ','
        '"codAtivacao"'
        ':'
        '"$_codAtivacao"'
        ','
        '"cnpj"'
        ':'
        '"$_cnpj"'
        ','
        '"cUF"'
        ':'
        '$_cUF';
  }
}
