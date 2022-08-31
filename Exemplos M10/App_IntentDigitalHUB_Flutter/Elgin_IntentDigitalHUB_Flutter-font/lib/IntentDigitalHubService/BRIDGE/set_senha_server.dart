import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class SetSenhaServer extends BridgeCommand {
  final String _senha;
  final bool _habilitada;

  SetSenhaServer(this._senha, this._habilitada) : super('SetSenhaServer');

  @override
  functionParameters() {
    return '"senha"'
        ':'
        '"$_senha"'
        ','
        '"habilitada"'
        ':'
        '$_habilitada';
  }
}
