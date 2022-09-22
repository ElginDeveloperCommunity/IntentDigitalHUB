part of 'intent_digital_hub_command_starter.dart';

abstract class IntentDigitalHubCommand {
  final String _functionName;
  final IntentDigitalHubModule _correspondingHubModule;

  IntentDigitalHubCommand(this._functionName, this._correspondingHubModule);

/**
     * Um comando do IDH deve ser um JSON com o seguinte formato: {funcao:"nomeDaFuncao", parametros:{}}.
     *
     * O subjson que deve ser inserido na chave "parametros" corresponde aos parametros específicos de cada função, caso a classe que herde desta (ou seja, representa um comando) possua parâmetros,
     * é necessário fazer o @override da função que define esses parâmetros {@link #functionParametersJson()}.
     * Exemplo: {funcao:"Corte",parametros:{linhas:10}}.
     *
     * Caso a classe comando não possua parâmetros, não é necessário realizar nenhuma implementação adicional, pois a implementação padrão de {@link #functionParametersJson()} oferece um json vazio,
     * fornecendo corretamente a chave "parametros" com um json vazio "{}", necessário para a formatação de comandos sem parâmetros.
     * Exemplo: {funcao:"FechaConexaoImpressora",parametros:{}}.
     */
  String get commandJSON =>
      //O Dart não possuí objeto JSON nativo, portanto, para que o JSON seja gerado corretamente, é necessário utilizar a função jsonEncode, que adicionará corretamente as aspas nos parâmetros string, formando assim um JSON válido.
      jsonEncode(
          {'funcao': _functionName, 'parametros': functionParametersJson});

  //Função que define os parâmetros específicos de cada comando, é utilizada para montagem do json de comando em getCommandJSON().
  Map<String, Object> get functionParametersJson => {};

  //Se um comando pertencer a uma lista, o simples método toString() saberá como definir cada comando na lista.
  @override
  String toString() => commandJSON;
}
