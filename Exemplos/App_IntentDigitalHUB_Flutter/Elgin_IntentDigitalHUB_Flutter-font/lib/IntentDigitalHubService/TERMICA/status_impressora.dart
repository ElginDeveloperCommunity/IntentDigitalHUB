import 'package:intent_digital_hub/IntentDigitalHubService/TERMICA/termica_commad.dart';

class StatusImpressora extends TermicaCommand {
  final int _param;

  StatusImpressora(this._param) : super('StatusImpressora');

  @override
  functionParameters() => '"param"' ':' '$_param';
}
