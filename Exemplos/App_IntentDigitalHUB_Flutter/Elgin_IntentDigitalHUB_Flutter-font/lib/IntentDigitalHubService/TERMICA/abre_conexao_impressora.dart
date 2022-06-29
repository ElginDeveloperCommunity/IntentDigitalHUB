import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class AbreConexaoImpressora extends TermicaCommand {
  final int _tipo;
  final String _modelo;
  final String _conexao;
  final int _parametro;

  AbreConexaoImpressora(
      this._tipo, this._modelo, this._conexao, this._parametro)
      : super('AbreConexaoImpressora');

  @override
  functionParameters() {
    return '"tipo"'
        ':'
        '$_tipo'
        ','
        '"modelo"'
        ':'
        '"$_modelo"'
        ','
        '"conexao"'
        ':'
        '"$_conexao"'
        ','
        '"parametro"'
        ':'
        '$_parametro';
  }
}
