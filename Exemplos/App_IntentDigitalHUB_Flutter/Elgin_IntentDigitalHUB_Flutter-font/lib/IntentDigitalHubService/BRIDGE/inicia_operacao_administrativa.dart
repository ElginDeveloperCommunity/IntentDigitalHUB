import 'package:intent_digital_hub/IntentDigitalHubService/BRIDGE/bridge_command.dart';

class IniciaOperacaoAdministrativa extends BridgeCommand {
  final int _idTransacao;
  final String _pdv;
  final int _operacao;

  IniciaOperacaoAdministrativa(this._idTransacao, this._pdv, this._operacao)
      : super('IniciaOperacaoAdministrativa');

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
        '"operacao"'
        ':'
        '$_operacao';
  }
}
