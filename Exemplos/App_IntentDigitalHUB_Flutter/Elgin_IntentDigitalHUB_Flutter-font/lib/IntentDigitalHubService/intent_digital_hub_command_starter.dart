import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'intent_digital_hub_module.dart';

part 'intent_digital_hub_command.dart';

//Classe utilizada para facilitar o start dos commandos do IDH
class IntentDigitalHubCommandStarter {
  //Previne a classe de ser instanciada
  IntentDigitalHubCommandStarter._();

  static const _platform = MethodChannel('elgin.intent_digital_hub');

  static Future startCommand(IntentDigitalHubCommand digitalHubCommand) {
    Map<String, dynamic> args = {
      'commandJSON': digitalHubCommand._commandJSON,
      'intentPath': digitalHubCommand._correspondingHubModule.intentPath,
    };

    return _platform.invokeMethod("startIntent", {"args": args});
  }

  //Concatena vários comandos
  static Future<String> startCommands(
      List<IntentDigitalHubCommand> digitalHubCommands) async {
    if (digitalHubCommands.isEmpty) {
      throw ArgumentError(
          'A lista de comandos a serem concatenadas não pode estar vazia!');
    }

    if (!_validateCommandList(digitalHubCommands)) {
      throw ArgumentError(
          'Todos os comandos da lista devem pertencer ao mesmo módulo!');
    }

    final commandJSON = _concatenatedDigitalHubCommand(digitalHubCommands);
    final IntentDigitalHubModule digitalHubIntentModule =
        digitalHubCommands.first._correspondingHubModule;

    Map<String, dynamic> args = {
      'commandJSON': commandJSON,
      'intentPath': digitalHubIntentModule.intentPath
    };

    return await _platform.invokeMethod("startIntent", {"args": args});
  }

  //Valida se os comandos a serem concatenados são do mesmo módulo
  static bool _validateCommandList(
      List<IntentDigitalHubCommand> digitalHubCommandList) {
    final IntentDigitalHubModule digitalHubIntentBase =
        digitalHubCommandList.first._correspondingHubModule;

    for (var digitalHubCommand in digitalHubCommandList) {
      if (digitalHubCommand._correspondingHubModule != digitalHubIntentBase) {
        return false;
      }
    }

    return true;
  }

  //Concatena os comandos em um único JSON de comando
  static String _concatenatedDigitalHubCommand(
      List<IntentDigitalHubCommand> digitalHubCommandList) {
    String concatenatedDigitalHubCommand = '';

    for (var digitalHubCommand in digitalHubCommandList) {
      //Remove o fechamento do JSON de todos os comandos da lista
      String actualDigitalHubCommandJSON = digitalHubCommand._commandJSON
          .substring(1, digitalHubCommand._commandJSON.length - 1);
      //Concatena e os subComandos e os separa por uma vírgula
      concatenatedDigitalHubCommand += actualDigitalHubCommandJSON + ',';
    }

    //Remove a ultima vírgula inserida
    concatenatedDigitalHubCommand = concatenatedDigitalHubCommand.substring(
        0, concatenatedDigitalHubCommand.length - 1);

    //Fecha o JSON novamente com os parênteses []
    return '[' + concatenatedDigitalHubCommand + ']';
  }
}
