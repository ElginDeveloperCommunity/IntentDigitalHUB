import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class IniciaVendaCredito extends BridgeCommand {
  final int _idTransacao;
  final String _pdv;
  final String _valorTotal;
  final int _tipoFinanciamento;
  final int _numeroParcelas;

  IniciaVendaCredito(this._idTransacao, this._pdv, this._valorTotal,
      this._tipoFinanciamento, this._numeroParcelas)
      : super('IniciaVendaCredito');

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
        '"$_valorTotal"'
        ','
        '"tipoFinanciamento"'
        ':'
        '$_tipoFinanciamento'
        ','
        '"numeroParcelas"'
        ':'
        '$_numeroParcelas';
  }
}
