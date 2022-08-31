import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class IniciaVendaDebito extends BridgeCommand {
  final int _idTransacao;
  final String _pdv;
  final String _valorTotal;

  IniciaVendaDebito(this._idTransacao, this._pdv, this._valorTotal)
      : super('IniciaVendaDebito');

  @override
  functionParameters() {
    return '"idTransacao"'
        ':'
        '$_idTransacao'
        ','
        '"pdv"'
        ':'
        '"$_pdv"'
        ','
        '"valorTotal"'
        ':'
        '"$_valorTotal"';
  }
}
