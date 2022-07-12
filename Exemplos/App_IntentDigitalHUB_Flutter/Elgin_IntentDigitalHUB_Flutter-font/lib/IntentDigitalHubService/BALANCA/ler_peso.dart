import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/balanca_command.dart';

class LerPeso extends BalancaCommand {
  final int _qtdLeituras;

  LerPeso(this._qtdLeituras) : super('LerPeso');

  @override
  String functionParameters() {
    return '"qtdLeituras"' ':' '$_qtdLeituras';
  }
}
