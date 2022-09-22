part of 'intent_digital_hub_command_starter.dart';

//Classe que representa um retorno do Intent Digital Hub, serve como deserializable do JSON de retorno do IDH.

class IntentDigitalHubCommandReturn {
  //Exemplo de json retorno do IntentDigitalHub
  //{"funcao":"AbreConexaoImpressora","mensagem":"Método executado","resultado":0}
  //Os campos funcao e mensagem serão sempre String.
  //O campo resultado, possuí tipo variável dependendo do módulo.
  final String funcao;
  final String mensagem;
  final dynamic resultado;

  IntentDigitalHubCommandReturn._({
    required this.funcao,
    required this.mensagem,
    this.resultado,
  });

  factory IntentDigitalHubCommandReturn.fromJson(Map<String, dynamic> json) {
    return IntentDigitalHubCommandReturn._(
      funcao: json['funcao'],
      mensagem: json['mensagem'],
      resultado: json['resultado'],
    );
  }

//Serializa uma lista de retornos de comando.
  static List<IntentDigitalHubCommandReturn> listOfFromJson(
      List<dynamic> jsonArray) {
    return jsonArray
        .map((commandJson) =>
            IntentDigitalHubCommandReturn.fromJson(commandJson))
        .toList();
  }
}
