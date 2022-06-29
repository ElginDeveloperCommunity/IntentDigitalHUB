import 'package:intent_digital_hub/IntentDigitalHubService/SAT/sat_command.dart';

class AssociarAssinatura extends SatCommand {
  final int _numSessao;
  final String _codAtivacao;
  final String _cnpjSH;
  final String _assinaturaAC;

  AssociarAssinatura(
      this._numSessao, this._codAtivacao, this._cnpjSH, this._assinaturaAC)
      : super('AssociarAssinatura');

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
        '"cnpjSH"'
        ':'
        '"$_cnpjSH"'
        ','
        '"assinaturaAC"'
        ':'
        '"$_assinaturaAC"';
  }
}
