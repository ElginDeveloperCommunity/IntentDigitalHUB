import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class ImpressaoCodigoBarras extends TermicaCommand {
  final int _tipo;
  final String _dados;
  final int _altura;
  final int _largura;
  // ignore: non_constant_identifier_names
  final int _HRI;

  ImpressaoCodigoBarras(
      this._tipo, this._dados, this._altura, this._largura, this._HRI)
      : super('ImpressaoCodigoBarras');

  @override
  functionParameters() {
    return '"tipo"'
        ':'
        '$_tipo'
        ','
        '"dados"'
        ':'
        '"$_dados"'
        ','
        '"altura"'
        ':'
        '$_altura'
        ','
        '"largura"'
        ':'
        '$_largura'
        ','
        '"HRI"'
        ':'
        '$_HRI';
  }
}
