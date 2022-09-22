import '../termica_command.dart';

class ImprimeXMLSAT extends TermicaCommand {
  final String _dados;
  final int _param;

  ImprimeXMLSAT(this._dados, this._param) : super('ImprimeXMLSAT');

  @override
  get functionParametersJson => {
        'dados': _dados,
        'param': _param,
      };
}
