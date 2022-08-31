import 'package:intent_digital_hub/IntentDigitalHubService/BALANCA/balanca_command.dart';

class ConfigurarProtocoloComunicacao extends BalancaCommand {
  final int _protocoloComunicacao;

  ConfigurarProtocoloComunicacao(this._protocoloComunicacao)
      : super('ConfigurarProtocoloComunicacao');

  @override
  String functionParameters() {
    return '"protocoloComunicacao"' ':' '$_protocoloComunicacao';
  }
}
