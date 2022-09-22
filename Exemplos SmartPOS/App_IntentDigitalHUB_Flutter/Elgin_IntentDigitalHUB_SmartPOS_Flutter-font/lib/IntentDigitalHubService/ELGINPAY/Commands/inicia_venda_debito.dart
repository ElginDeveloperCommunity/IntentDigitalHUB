import 'package:flutter_smartpos/IntentDigitalHubService/ELGINPAY/elginpay_command.dart';

class IniciaVendaDebito extends ElginPayCommand {
  final String _valorTotal;

  IniciaVendaDebito(this._valorTotal) : super('iniciaVendaDebito');

  @override
  get functionParametersJson => {
        'valorTotal': _valorTotal,
      };
}
