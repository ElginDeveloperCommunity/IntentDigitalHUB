import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/balanca_command.dart';

class ConfigurarModeloBalanca extends BalancaCommand {
  final int _modeloBalanca;

  ConfigurarModeloBalanca(this._modeloBalanca)
      : super('ConfigurarModeloBalanca');

  @override
  String functionParameters() {
    return '"modeloBalanca"' ':' '$_modeloBalanca';
  }
}
