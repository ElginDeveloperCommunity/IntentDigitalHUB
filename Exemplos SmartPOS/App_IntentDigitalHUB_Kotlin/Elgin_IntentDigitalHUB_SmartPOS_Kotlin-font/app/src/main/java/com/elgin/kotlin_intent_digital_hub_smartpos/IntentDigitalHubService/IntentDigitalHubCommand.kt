package com.elgin.kotlin_intent_digital_hub_smartpos.IntentDigitalHubService

import com.google.gson.JsonObject


/**
 * Classe abstrata que generaliza as carecterísticas comuns à todos os comandos do Intent Digital Hub, as classes que herdam desta implementam cada comando de maneira específica servindo como Wrapper
 */
abstract class IntentDigitalHubCommand
    (//Nome da função
    protected val functionName: String, //Módulo a qual a função pertence
    val correspondingIntentModule: IntentDigitalHubModule
) {

    /**
     * Um comando do IDH deve ser um JSON com o seguinte formato: {funcao:"nomeDaFuncao", parametros:{}}.
     *
     * O subjson que deve ser inserido na chave "parametros" corresponde aos parametros específicos de cada função, caso a classe que herde desta (ou seja, representa um comando) possua parâmetros,
     * é necessário fazer o @override da função que define esses parâmetros [.functionParametersJson].
     * Exemplo: {funcao:"Corte",parametros:{linhas:10}}.
     *
     * Caso a classe comando não possua parâmetros, não é necessário realizar nenhuma implementação adicional, pois a implementação padrão de [.functionParametersJson] oferece um json vazio,
     * fornecendo corretamente a chave "parametros" com um json vazio "{}", necessário para a formatação de comandos sem parâmetros.
     * Exemplo: {funcao:"FechaConexaoImpressora",parametros:{}}.
     */
    fun getCommandJSON(): JsonObject {
        val commandJson = JsonObject()
        commandJson.addProperty("funcao", functionName)
        commandJson.add("parametros", functionParametersJson())
        return commandJson
    }

    /**
     * Função que define os parametros especificos de cada comando, é utilizada para montagem do json de comando em getCommandJSON().
     */
    protected open fun functionParametersJson(): JsonObject? {
        return JsonObject()
    }
}