import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'intent_digital_hub_module.dart';

part 'intent_digital_hub_command.dart';
part 'intent_digital_hub_command_return.dart';

//Classe service utilizada para executar o inicio da intent que carrega o comando Bridge
class IntentDigitalHubCommandStarter {
  //Previne a classe de ser instanciada
  IntentDigitalHubCommandStarter._();

  static const _platform = MethodChannel('elgin.intent_digital_hub');

  //Inicia um comando do IDH. Retorna um objeto serializado com o resultado do comando.
  static Future<IntentDigitalHubCommandReturn> startCommand(
      IntentDigitalHubCommand digitalHubCommand) async {
    Map<String, String> args = {
      //O IntentDigitalHub espera um array de json, como esta função lida com apenas uma função, é necessário a formatação de array adicional, por isto os '[' e ']';
      'commandJSON': '[' + digitalHubCommand.commandJSON + ']',
      'intentPath': digitalHubCommand._correspondingHubModule.intentPath,
    };

    //O json de retorno do idh é um array de json, sendo que cada json corresponde a uma resposta do idh para o comando enviado.
    final String idhCommandReturnJson =
        await _platform.invokeMethod("startIntent", {"args": args});

    //Como esta função envia apenas um comando, o json de retorno é um array de 1 posição, sendo que a posição 0 corresponde a resposta do idh para o comando enviado.
    return IntentDigitalHubCommandReturn.fromJson(
        jsonDecode(idhCommandReturnJson)[0]);
  }

  //Inicia múltiplos comandos do IDH. Retorna uma lista de objetos serializados com o resultado dos comandos.
  static Future<List<IntentDigitalHubCommandReturn>> startCommands(
      List<IntentDigitalHubCommand> digitalHubCommands) async {
    if (digitalHubCommands.isEmpty) {
      throw ArgumentError(
          'A lista de comandos a serem concatenadas não pode estar vazia!');
    }

    if (!_isCommandListValid(digitalHubCommands)) {
      throw ArgumentError(
          'Todos os comandos da lista devem pertencer ao mesmo módulo!');
    }

    Map<String, String> args = {
      //O método toString() foi definido para um comando, como esta função já lida com uma lista de comandos, basta o método toString() para a formatação.
      'commandJSON': digitalHubCommands.toString(),
      //O path de modulo da intent pode ser extraído de qualquer comando da lista, uma vez que a lista foi validada para que todos os comandos pertençam ao mesmo módulo.
      'intentPath': digitalHubCommands[0]._correspondingHubModule.intentPath,
    };

    //O json de retorno do idh é um array de json, sendo que cada json corresponde a uma resposta do idh para o comando enviado.
    final String idhCommandReturnJson =
        await _platform.invokeMethod("startIntent", {"args": args});

    //Como esta função envia uma lista de comandos, o json de retorno é um array de n posições, sendo que cada posição corresponde a uma resposta do idh para o comando enviado.
    return IntentDigitalHubCommandReturn.listOfFromJson(
        jsonDecode(idhCommandReturnJson));
  }

  //Valida se os comandos a serem concatenados são do mesmo módulo
  static bool _isCommandListValid(
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

  //Para iniciar o scanner do IDH, não se faz necessário nenhum comando, pois a única ação disponível para este modulo é o inicio do scanner.
  static Future<IntentDigitalHubCommandReturn> startScanner() async {
    Map<String, String> args = {
      'intentPath': IntentDigitalHubModule.SCANNER.intentPath
    };
    final String idhCommandReturnJson =
        await _platform.invokeMethod("startIntent", {"args": args});
    return IntentDigitalHubCommandReturn.fromJson(
        jsonDecode(idhCommandReturnJson)[0]);
  }
}
