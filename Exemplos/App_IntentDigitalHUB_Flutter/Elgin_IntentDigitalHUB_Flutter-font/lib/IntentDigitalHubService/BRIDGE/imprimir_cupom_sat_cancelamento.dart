import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class ImprimirCupomSatCancelamento extends BridgeCommand {
  final String _xml;
  final String _assQRCode;

  ImprimirCupomSatCancelamento(this._xml, this._assQRCode)
      : super('ImprimirCupomSatCancelamento');

  @override
  functionParameters() {
    return '"xml"'
        ':'
        '"'
        '$_xml'
        '"'
        ','
        '"assQRCode"'
        ':'
        '"$_assQRCode"';
  }
}
