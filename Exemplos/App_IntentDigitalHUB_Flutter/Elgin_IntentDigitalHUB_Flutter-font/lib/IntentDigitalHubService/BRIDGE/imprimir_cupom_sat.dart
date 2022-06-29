import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class ImprimirCupomSat extends BridgeCommand {
  final String _xml;

  ImprimirCupomSat(this._xml) : super("ImprimirCupomSat");

  @override
  functionParameters() => '"xml"' ':' '"' '$_xml' '"';
}
