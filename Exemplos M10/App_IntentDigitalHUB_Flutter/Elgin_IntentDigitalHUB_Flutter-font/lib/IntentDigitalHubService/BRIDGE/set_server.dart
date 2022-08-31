import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class SetServer extends BridgeCommand {
  final String _ipTerminal;
  final int _portaTransacao;
  final int _portaStatus;

  SetServer(this._ipTerminal, this._portaTransacao, this._portaStatus)
      : super('SetServer');

  @override
  functionParameters() {
    return '"ipTerminal"'
        ':'
        '"$_ipTerminal"'
        ','
        '"portaTransacao"'
        ':'
        '$_portaTransacao'
        ','
        '"portaStatus"'
        ':'
        '$_portaStatus';
  }
}
