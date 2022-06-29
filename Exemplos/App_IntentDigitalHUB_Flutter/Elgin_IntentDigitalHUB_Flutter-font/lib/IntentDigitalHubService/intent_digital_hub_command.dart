part of 'intent_digital_hub_command_starter.dart';

abstract class IntentDigitalHubCommand {
  final String _functionName;
  final IntentDigitalHubModule _correspondingHubModule;

  IntentDigitalHubCommand(this._functionName, this._correspondingHubModule);

  //JSON do comando formatado
  String get _commandJSON {
    return '[{'
            '"funcao"'
            ':'
            '"' +
        _functionName +
        '"'
            ','
            '"parametros"'
            ':'
            '{' +
        functionParameters() +
        '}'
            '}]';
  }

  //Função que deve ser implementada pelas subsclasses definindo a formatação do JSON dos paramêtros
  @protected
  String functionParameters();
}
