import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class ImprimeImagem extends TermicaCommand {
  final String _path;

  ImprimeImagem(this._path) : super('ImprimeImagem');

  @override
  functionParameters() => '"path"' ':' '"$_path"';
}
