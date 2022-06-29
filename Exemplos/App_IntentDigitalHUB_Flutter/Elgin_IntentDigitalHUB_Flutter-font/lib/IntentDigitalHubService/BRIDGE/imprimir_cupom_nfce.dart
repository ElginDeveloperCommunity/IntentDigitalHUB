import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class ImprimirCupomNfce extends BridgeCommand {
  final String _xml;
  final int _indexcsc;
  final String _csc;

  ImprimirCupomNfce(this._xml, this._indexcsc, this._csc)
      : super('ImprimirCupomNfce');

  @override
  functionParameters() {
    return '"xml"'
        ':'
        '"'
        '$_xml'
        '"'
        ','
        '"indexcsc"'
        ':'
        '$_indexcsc'
        ','
        '"csc"'
        ':'
        '"$_csc"';
  }
}
