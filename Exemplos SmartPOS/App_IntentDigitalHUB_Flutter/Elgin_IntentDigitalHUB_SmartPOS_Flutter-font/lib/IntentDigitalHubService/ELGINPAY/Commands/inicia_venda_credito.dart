import 'package:flutter_smartpos/IntentDigitalHubService/ELGINPAY/elginpay_command.dart';

class IniciaVendaCredito extends ElginPayCommand {
  final String _valorTotal;
  final int _tipoFinanciamento;
  final int _numeroParcelas;

  IniciaVendaCredito(
      this._valorTotal, this._tipoFinanciamento, this._numeroParcelas)
      : super('iniciaVendaCredito');

  @override
  get functionParametersJson => {
        'valorTotal': _valorTotal,
        'tipoFinanciamento': _tipoFinanciamento,
        'numeroParcelas': _numeroParcelas,
      };
}
