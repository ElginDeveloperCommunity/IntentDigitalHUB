import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class ImprimeXMLSAT extends TermicaCommand {
  final String _dados;
  final int _param;

  ImprimeXMLSAT(this._dados, this._param) : super('ImprimeXMLSAT');

  @override
  functionParameters() {
    return '"dados"'
        ':'
        '"'
        '$_dados'
        '"'
        ','
        '"param"'
        ':'
        '$_param';
  }
}
