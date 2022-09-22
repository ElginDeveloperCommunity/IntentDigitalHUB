import '../termica_command.dart';

class ImprimeImagem extends TermicaCommand {
  final String _path;

  ImprimeImagem(this._path) : super('ImprimeImagem');

  @override
  get functionParametersJson => {
        'path': _path,
      };
}
