import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class ImprimeXMLNFCe extends TermicaCommand {
  final String _dados;
  final int _indexcsc;
  final String _csc;
  final int _param;

  ImprimeXMLNFCe(this._dados, this._indexcsc, this._csc, this._param)
      : super('ImprimeXMLNFCe');

  @override
  functionParameters() {
    return '"dados"'
        ':'
        '"'
        '$_dados'
        '"'
        ','
        '"indexcsc"'
        ':'
        '$_indexcsc'
        ','
        '"csc"'
        ':'
        '"$_csc"'
        ','
        '"param"'
        ':'
        '$_param';
  }
}
