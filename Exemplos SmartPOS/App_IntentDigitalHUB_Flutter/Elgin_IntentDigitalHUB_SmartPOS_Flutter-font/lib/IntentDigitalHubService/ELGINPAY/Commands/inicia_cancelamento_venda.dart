import 'package:flutter_smartpos/IntentDigitalHubService/ELGINPAY/elginpay_command.dart';

class IniciaCancelamentoVenda extends ElginPayCommand {
  final String _valorTotal;
  final String _ref;
  final String _data;

  IniciaCancelamentoVenda(this._valorTotal, this._ref, this._data)
      : super('iniciaCancelamentoVenda');

  @override
  get functionParametersJson => {
        'valorTotal': _valorTotal,
        'ref': _ref,
        'data': _data,
      };
}
