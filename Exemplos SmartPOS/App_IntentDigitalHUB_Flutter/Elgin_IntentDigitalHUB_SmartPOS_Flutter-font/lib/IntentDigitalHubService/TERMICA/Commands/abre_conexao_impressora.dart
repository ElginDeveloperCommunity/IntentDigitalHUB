import 'package:flutter_smartpos/IntentDigitalHubService/TERMICA/termica_command.dart';

class AbreConexaoImpressora extends TermicaCommand {
  final int _tipo;
  final String _modelo;
  final String _conexao;
  final int _parametro;

  AbreConexaoImpressora(
      this._tipo, this._modelo, this._conexao, this._parametro)
      : super('AbreConexaoImpressora');

  @override
  get functionParametersJson => {
        'tipo': _tipo,
        'modelo': _modelo,
        'conexao': _conexao,
        'parametro': _parametro,
      };
}
