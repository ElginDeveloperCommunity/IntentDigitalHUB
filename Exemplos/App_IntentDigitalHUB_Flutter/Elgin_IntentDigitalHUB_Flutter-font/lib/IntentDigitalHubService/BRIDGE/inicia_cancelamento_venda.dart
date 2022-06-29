import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class IniciaCancelamentoVenda extends BridgeCommand {
  final int _idTransacao;
  final String _pdv;
  final String _valorTotal;
  final String _dataHora;
  final String _nsu;

  IniciaCancelamentoVenda(
      this._idTransacao, this._pdv, this._valorTotal, this._dataHora, this._nsu)
      : super('IniciaCancelamentoVenda');

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
        '"dataHora"'
        ':'
        '"$_dataHora"'
        ','
        '"nsu"'
        ':'
        '"$_nsu"';
  }
}
