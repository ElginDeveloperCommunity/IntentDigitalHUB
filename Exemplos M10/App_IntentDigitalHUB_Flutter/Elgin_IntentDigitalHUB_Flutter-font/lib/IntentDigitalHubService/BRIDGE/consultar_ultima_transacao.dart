import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class ConsultarUltimaTransacao extends BridgeCommand {
  final String _pdv;

  ConsultarUltimaTransacao(this._pdv) : super('ConsultarUltimaTransacao');

  @override
  functionParameters() => '"pdv"' ':' '"$_pdv"';
}
