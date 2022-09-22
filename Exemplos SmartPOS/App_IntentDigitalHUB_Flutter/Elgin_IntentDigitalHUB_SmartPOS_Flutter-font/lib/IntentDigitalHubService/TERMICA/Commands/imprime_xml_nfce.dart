import '../termica_command.dart';

class ImprimeXMLNFCe extends TermicaCommand {
  final String _dados;
  final int _indexcsc;
  final String _csc;
  final int _param;

  ImprimeXMLNFCe(this._dados, this._indexcsc, this._csc, this._param)
      : super('ImprimeXMLNFCe');

  @override
  get functionParametersJson => {
        'dados': _dados,
        'indexcsc': _indexcsc,
        'csc': _csc,
        'param': _param,
      };
}
