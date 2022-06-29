import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class SetSenha extends BridgeCommand {
  final String _senha;
  final bool _habilitada;

  SetSenha(this._senha, this._habilitada) : super('SetSenha');

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
