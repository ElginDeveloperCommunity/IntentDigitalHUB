import '../termica_command.dart';

class StatusImpressora extends TermicaCommand {
  final int _param;

  StatusImpressora(this._param) : super('StatusImpressora');

  @override
  get functionParametersJson => {
        'param': _param,
      };
}
